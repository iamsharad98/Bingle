package in.komu.komu.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import in.komu.komu.Home.MediaActivity;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.Models.users_profile;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;

public class ProfileVideoListAdapter extends ArrayAdapter<Video> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;


    private static final String TAG = "ProfileVideoListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private StringBuilder mUserStringBuilder;

    public ProfileVideoListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Video> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;

        mReference = FirebaseDatabase.getInstance().getReference();

//        for(Photo photo: objects){
//            Log.d(TAG, "MainFeedListAdapter: photo id: " + photo.getPhoto_id());
//        }
    }

    static class ViewHolder{
        String likesString;
        TextView  timeDetla, caption,username, likes, comments, tvUsername, textLike;
//        VideoView videoView;
        ImageView profilePhoto,  heartRed, heartWhite, comment, playVideo, pauseVideo, fullscreenVideo, videoPost;
        FrameLayout videoSection;

        user_account_setting settings = new user_account_setting();
        users_profile user  = new users_profile();
        StringBuilder users;
        String mLikesString;
        boolean likeByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Video videoModel;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.profilePhoto = (ImageView) convertView.findViewById(R.id.profile_photo);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.videoPost = (ImageView) convertView.findViewById(R.id.videoPost);
            holder.heartRed = (ImageView) convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite = (ImageView) convertView.findViewById(R.id.image_heart);
            holder.comment = (ImageView) convertView.findViewById(R.id.ic_comment);
            holder.likes = (TextView) convertView.findViewById(R.id.tvLikes);
            holder.comments = (TextView) convertView.findViewById(R.id.image_comments_link);
            holder.caption = (TextView) convertView.findViewById(R.id.imgCaption);
            holder.timeDetla = (TextView) convertView.findViewById(R.id.image_time_posted);
            holder.playVideo = (ImageView) convertView.findViewById(R.id.playVideo);
            holder.pauseVideo = (ImageView) convertView.findViewById(R.id.pauseVideo);
//            holder.fullscreenVideo = (ImageView) convertView.findViewById(R.id.fullscreenVideo);
            holder.videoSection = (FrameLayout) convertView.findViewById(R.id.videoSection);
            holder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            holder.textLike = (TextView) convertView.findViewById(R.id.textLike);




            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.videoModel = getItem(position);
        holder.detector = new GestureDetector(mContext, new GestureListener(holder));
        holder.users = new StringBuilder();
        holder.heart = new Heart(holder.heartWhite, holder.heartRed);

        //get the current users username (need for checking likes string)
        getCurrentUsername();

        //get likes string
        getLikesString(holder);

        //set the caption
        holder.caption.setText(getItem(position).getCaption());

        //set the comment
        List<Comment> comments = getItem(position).getComments();
        holder.comments.setText("View all " + comments.size() + " comments");
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for " + getItem(position).getVideo_id());
                ((activity_profile)mContext).onCommentThreadSelectedListener(getItem(position));

                //going to need to do something else?
//                ((activity_profile)mContext).hideLayout();

            }
        });

        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDetla.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeDetla.setText("TODAY");
        }

        //get the profile image and username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                   // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(user_account_setting.class).getUsername());



                    holder.settings = singleSnapshot.getValue(user_account_setting.class);
                    holder.user = singleSnapshot.getValue(users_profile.class);

                    holder.username.setText(singleSnapshot.getValue(user_account_setting.class).getUsername());
                    holder.tvUsername.setText(singleSnapshot.getValue(user_account_setting.class).getUsername());

                    Glide.with(getContext().getApplicationContext())
                            .load(singleSnapshot.getValue(users_profile.class).getProfile_photo())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.loading_image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                            )
                            .into(holder.profilePhoto);


                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((activity_profile)mContext).onCommentThreadSelectedListener(getItem(position));

                            //another thing?
//                            ((HomeActivity)mContext).hideLayout();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get the user object
        Query userQuery = mReference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(getItem(position).getUser_id());
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                    singleSnapshot.getValue(users_profile.class).getUsername());

                    holder.user = singleSnapshot.getValue(users_profile.class);
                    holder.settings = singleSnapshot.getValue(user_account_setting.class);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(reachedEndOfList(position)){
            loadMoreData();
        }


        Glide.with(getContext().getApplicationContext())
                .load(getItem(position).getThumb_url())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .into(holder.videoPost);

        holder.videoPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MediaActivity.class);
                intent.putExtra(getContext().getString(R.string.field_video_url) , getItem(position).getVideo_url() );
                getContext().startActivity(intent);
//                Toast.makeText(mContext, "Video Url "  + getItem(position).getVideo_url() , Toast.LENGTH_SHORT).show();

            }
        });

//        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//                holder.videoView.start();
//            }
//        });


        return convertView;
    }


    private boolean reachedEndOfList(int position){
        return position == getCount() - 1;
    }

    private void loadMoreData(){

        try{
            mOnLoadMoreItemsListener = (OnLoadMoreItemsListener) getContext();
        }catch (ClassCastException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems();
        }catch (NullPointerException e){
            Log.e(TAG, "loadMoreData: ClassCastException: " +e.getMessage() );
        }
    }



    private void addNewLike(final ViewHolder holder){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(holder.user.getUser_id());

        mReference.child(mContext.getString(R.string.db_videos))
                .child(holder.videoModel.getUser_id())
                .child(holder.videoModel.getVideo_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        holder.heart.toggleLike();
        getLikesString(holder);
    }

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    currentUsername = singleSnapshot.getValue(user_account_setting.class).getUsername();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikesString(final ViewHolder holder){
        Log.d(TAG, "getLikesString: getting likes string");

        Log.d(TAG, "getLikesString: photo id: " + holder.videoModel.getVideo_id());
        try{
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_videos))
                .child(holder.user.getUser_id())
                .child(holder.videoModel.getVideo_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserStringBuilder = new StringBuilder();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference
                            .child(mContext.getString(R.string.db_users_profile))
                            .orderByChild(mContext.getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                                Log.d(TAG, "onDataChange: found like: " +
                                        singleSnapshot.getValue(users_profile.class).getUsername());

                                mUserStringBuilder.append(singleSnapshot.getValue(users_profile.class).getUsername());
                                mUserStringBuilder.append(",");
                            }

                            String[] splitUsers = holder.users.toString().split(",");

                            if(mUserStringBuilder.toString().contains(currentUsername + ",")){//mitch, mitchell.tabian
                                holder.likeByCurrentUser = true;
                            }else{
                                holder.likeByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            if (length == 0){
                                holder.textLike.setVisibility(View.VISIBLE);
                                holder.likes.setVisibility(View.GONE);

                            }
                            else if(length == 1){
                                holder.likesString = "Liked by " + splitUsers[0];
                            }
                            else if(length == 2){
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + " and " + splitUsers[1];
                            }
                            else if(length == 3){
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + " and " + splitUsers[2];

                            }
                            else if(length == 4){
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + splitUsers[3];
                            }
                            else if(length > 4){
                                holder.likesString = "Liked by " + splitUsers[0]
                                        + ", " + splitUsers[1]
                                        + ", " + splitUsers[2]
                                        + " and " + (splitUsers.length - 3) + " others";
                            }
                            Log.d(TAG, "onDataChange: likes string: " + holder.likesString);
                            //setup likes string
                            setupLikesString(holder, holder.likesString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if(!dataSnapshot.exists()){
                    holder.likesString = "";
                    holder.likeByCurrentUser = false;
                    //setup likes string
                    setupLikesString(holder, holder.likesString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }catch (NullPointerException e){
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage() );
            holder.likesString = "";
            holder.likeByCurrentUser = false;
            //setup likes string
            setupLikesString(holder, holder.likesString);
        }
    }
    public class GestureListener extends GestureDetector.SimpleOnGestureListener{

        ViewHolder mHolder;
        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            Log.d(TAG, "onDoubleTap: clicked on photo: " + mHolder.videoModel.getVideo_id());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.db_videos))
                    .child(mHolder.user.getUser_id())
                    .child(mHolder.videoModel.getVideo_id())
                    .child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
                        if (mHolder.likeByCurrentUser
                             &&  singleSnapshot.getValue(Like.class).getUser_id()
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                ) {

                            Toast.makeText(mContext, "like by current user" + mHolder.likeByCurrentUser,
                                    Toast.LENGTH_SHORT).show();
                            mReference.child(mContext.getString(R.string.db_videos))
                                    .child(mHolder.user.getUser_id())
                                    .child(mHolder.videoModel.getVideo_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mHolder.heart.toggleLike();
                            getLikesString(mHolder);
                        }
                        //case2: The user has not liked the photo
                        else if (!mHolder.likeByCurrentUser) {
                            //add new like
                            addNewLike(mHolder);
                            break;
                        }
                    }
                    if (!dataSnapshot.exists()) {
                        //add new like
                        addNewLike(mHolder);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void setupLikesString(final ViewHolder holder, String likesString){
        Log.d(TAG, "setupLikesString: likes string:" + holder.likesString);

        Log.d(TAG, "setupLikesString: photo id: " + holder.videoModel.getVideo_id());
        if(holder.likeByCurrentUser){
            Log.d(TAG, "setupLikesString: photo is liked by current user");
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }else{
            Log.d(TAG, "setupLikesString: photo is not liked by current user");
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartRed.setVisibility(View.GONE);
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return holder.detector.onTouchEvent(event);
                }
            });
        }
        holder.likes.setText(likesString);
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Video video){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String videoTimestamp = video.getTimestamp();
        try{
            timestamp = sdf.parse(videoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}

// For video list in listview
//String videoPath = getItem(position).getVideo_url();
//        Uri uri = Uri.parse(videoPath);
//        holder.videoView.setVideoURI(uri);
//        holder.videoView.requestFocus();
//        holder.videoView.pause();
//
//        holder.playVideo.setVisibility(View.VISIBLE);
//        holder.fullscreenVideo.setVisibility(View.VISIBLE);
//        holder.pauseVideo.setVisibility(View.GONE);
//        holder.playVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.videoView.start();
//                holder.playVideo.setVisibility(View.GONE);
//                holder.pauseVideo.setVisibility(View.GONE);
//                holder.fullscreenVideo.setVisibility(View.GONE);
//                isplaying = true;
//
//
//            }
//        });
//
//
//
//
//            holder.videoSection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isplaying) {
//                        holder.playVideo.setVisibility(View.GONE);
//                        holder.pauseVideo.setVisibility(View.VISIBLE);
//                        holder.fullscreenVideo.setVisibility(View.VISIBLE);
//                        holder.pauseVideo.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                holder.videoView.pause();
//                                isplaying = false;
//                                holder.playVideo.setVisibility(View.VISIBLE);
//                                holder.fullscreenVideo.setVisibility(View.VISIBLE);
//                                holder.pauseVideo.setVisibility(View.GONE);
//                            }
//                        });
////                        holder.playVideo.setVisibility(View.GONE);
////                        holder.pauseVideo.setVisibility(View.GONE);
////                        holder.fullscreenVideo.setVisibility(View.GONE);
////
//
////                    holder.fullscreenVideo.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            //Code
////                        }
////                    });
//                    }
//
//
//                }
//            });
//
//
//
//        if (!isplaying){
//            holder.playVideo.setVisibility(View.VISIBLE);
//            holder.pauseVideo.setVisibility(View.GONE);
//            holder.fullscreenVideo.setVisibility(View.VISIBLE);
//            holder.playVideo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.videoView.start();
//                    isplaying = true;
//                    holder.playVideo.setVisibility(View.GONE);
//                    holder.fullscreenVideo.setVisibility(View.GONE);
//                    holder.pauseVideo.setVisibility(View.GONE);
//
//                }
//            });
//        }


























