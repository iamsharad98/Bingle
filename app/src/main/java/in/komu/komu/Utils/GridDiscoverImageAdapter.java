package in.komu.komu.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.komu.komu.Home.ActivityContestGrid;
import in.komu.komu.Home.MediaActivity;
import in.komu.komu.MainActivity;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Notification;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.Models.users_profile;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Search.SearchActivity;

public class GridDiscoverImageAdapter extends ArrayAdapter<Video> {

    private static final String TAG = "GridDiscoverImageAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private MediaController videoMediaController;
    private List<Video> mVideos;
    private boolean isplaying;
    private List<Like> likes;


    public GridDiscoverImageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Video> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mContext = context;

        mReference = FirebaseDatabase.getInstance().getReference();

    }


    static class ViewHolder{
        TextView timeDetla, caption,username, duration, category, follow, likeCount, commentCount;
        ImageView mPostImage, profilePhoto, heart_white, heart_red, heart_red_small, comment_big, comment_small, shareIcon;

        user_account_setting settings = new user_account_setting();
        users_profile user  = new users_profile();
        boolean likeByCurrentUser;
        GestureDetector detector;
        Video video;
        users_profile userProfile = new users_profile();
        user_account_setting accountSetting  = new user_account_setting();
        DatabaseReference myRef1;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.heart_white = (ImageView) convertView.findViewById(R.id.image_heart);
            holder.heart_red = (ImageView) convertView.findViewById(R.id.image_heart_red);
            holder.heart_red_small = (ImageView) convertView.findViewById(R.id.miniRedHeart);
            holder.comment_big = (ImageView) convertView.findViewById(R.id.ic_comment);
            holder.comment_small = (ImageView) convertView.findViewById(R.id.miniComment);
            holder.likeCount = (TextView) convertView.findViewById(R.id.likeCount);
            holder.commentCount = (TextView) convertView.findViewById(R.id.commentCount);
            holder.profilePhoto = (ImageView) convertView.findViewById(R.id.profile_photo);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.caption = (TextView) convertView.findViewById(R.id.imgCaption);
            holder.timeDetla = (TextView) convertView.findViewById(R.id.dateStamp);
//            holder.duration = (TextView) convertView.findViewById(R.id.duration);
            holder.mPostImage = (ImageView) convertView.findViewById(R.id.postImage);
            holder.category = (TextView) convertView.findViewById(R.id.contestCategory);
            holder.follow = (TextView) convertView.findViewById(R.id.follow);
            holder.shareIcon = (ImageView) convertView.findViewById(R.id.shareIcon);


            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.video = getItem(position);
//        holder.detector = new GestureDetector(mContext, new GestureListener(holder));

        //set the share Icon
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(getItem(position));
            }
        });


        //set the caption
        holder.caption.setText(getItem(position).getCaption());
        holder.myRef1 = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.db_videos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        ;
        holder.myRef1.keepSynced(true);


        onClickLike(holder, getItem(position));
        isLiking(holder);


        isFollowing(getItem(position), holder);

        likes = getItem(position).getLikes();
//        Toast.makeText(mContext, "Likes " + likes.size(), Toast.LENGTH_SHORT).show();

        if ((null == likes) && (likes.isEmpty()) ) {
            holder.likeCount.setText(String.valueOf(0));

        }else{
            holder.likeCount.setText(String.valueOf(likes.size()));
        }

        if (getItem(position).getUser_id()
                .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            setFollowing(holder);
        }else{

        }

        //set the comment
        List<Comment> comments = getItem(position).getComments();
        holder.commentCount.setText(String.valueOf(comments.size()));
        holder.commentCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (getContext().getClass().getSimpleName().equals(mContext.getString(R.string.main_activity)) ){
                    ((MainActivity)mContext).onCommentThreadSelected(getItem(position),
                            mContext.getString(R.string.newsfeed_video));

                    //going to need to do something else?
                    ((MainActivity)mContext).hideLayout();
                }else if (getContext().getClass().getSimpleName().equals(mContext.getString(R.string.ActivityContestGrid))) {
                    ((ActivityContestGrid) mContext).onCommentThreadSelected(getItem(position),
                            mContext.getString(R.string.newsfeed_video));

                    //going to need to do something else?
                    ((ActivityContestGrid) mContext).hideLayout();
                }

            }
        });

        //set the follow button
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.follow.setVisibility(View.GONE);
//                mUnfollow.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference()
                        .child(mContext.getString(R.string.db_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getItem(position).getUser_id())
                        .child(mContext.getString(R.string.field_user_id))
                        .setValue(getItem(position).getUser_id());

                FirebaseDatabase.getInstance().getReference()
                        .child(mContext.getString(R.string.db_follower))
                        .child(getItem(position).getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mContext.getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });


        //set the duration
//        String videoDuration = getItem(position).getDuration();
//        holder.duration.setText(videoDuration);

        holder.category.setText(getItem(position).getCategory());

        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timeDetla.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.timeDetla.setText("TODAY");
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //get the profile image and username
        Query query = reference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    // currentUsername = singleSnapshot.getValue(UserAccountSettings.class).getUsername();

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(user_account_setting.class).getUsername());



                    holder.settings = singleSnapshot.getValue(user_account_setting.class);
                    holder.user = singleSnapshot.getValue(users_profile.class);
                    holder.username.setText(singleSnapshot.getValue(user_account_setting.class).getUsername());

                    //Set on Click
                    final user_account_setting userSetting = new user_account_setting();
                    userSetting.setUser_id(getItem(position).getUser_id());
                    userSetting.setUsername(singleSnapshot.getValue(user_account_setting.class).getUsername());
                    userSetting.setEmail(singleSnapshot.getValue(user_account_setting.class).getEmail());
                    userSetting.setPhone_number(singleSnapshot.getValue(user_account_setting.class).getPhone_number());


                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =  new Intent(getContext(), activity_profile.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.search_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), userSetting);
                            mContext.startActivity(intent);
                        }
                    });

                    //Profile Photo
                    Glide.with(getContext().getApplicationContext())
                            .load(singleSnapshot.getValue(users_profile.class).getProfile_photo())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.loading_image)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                            )
                            .into(holder.profilePhoto);

                    holder.profilePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =  new Intent(getContext(), activity_profile.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.search_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), userSetting);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.userProfile = singleSnapshot.getValue(users_profile.class);
                    holder.accountSetting = singleSnapshot.getValue(user_account_setting.class);
                    holder.comment_big.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getContext().getClass().getSimpleName().equals(mContext.getString(R.string.main_activity)) ){
                                ((MainActivity)mContext).onCommentThreadSelected(getItem(position),
                                        mContext.getString(R.string.newsfeed_video));

                                //going to need to do something else?
                                ((MainActivity)mContext).hideLayout();
                            }else if (getContext().getClass().getSimpleName().equals(mContext.getString(R.string.ActivityContestGrid))){
                                ((ActivityContestGrid)mContext).onCommentThreadSelected(getItem(position),
                                        mContext.getString(R.string.newsfeed_video));

                                //going to need to do something else?
                                ((ActivityContestGrid)mContext).hideLayout();
                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Glide.with(mContext)
                .load(getItem(position).getThumb_url())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .into(holder.mPostImage);

        holder.mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MediaActivity.class);
                intent.putExtra(getContext().getString(R.string.field_video_url) , getItem(position).getVideo_url() );
                getContext().startActivity(intent);
            }
        });


        //get the user object
        Query userQuery = mReference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(getItem(position).getUser_id());
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(users_profile.class).getUsername());

                    holder.userProfile = singleSnapshot.getValue(users_profile.class);
                    holder.user = singleSnapshot.getValue(users_profile.class);
                    holder.settings = singleSnapshot.getValue(user_account_setting.class);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }

    // Share Method
    @JavascriptInterface
    public void sendMessage(Video video) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//        Uri uri = Uri.parse(video.getThumb_url());
        String message = "I've found a App bingle in which we " +
                "can share our talent like singing, dancing, etc to the world and win cash prizes upto 1 Lakh that might be interested you," +
                " https://bit.ly/2wBh3yV";

        emailIntent.setData(Uri.parse("shareTo:"));
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bingle - Discover Talent");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message );
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.setType("text/plain");
        getContext().startActivity(Intent.createChooser(emailIntent, "Send to friend"));
    }




    @SuppressLint("LongLogTag")
    private void setFollowing(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        holder.follow.setVisibility(View.GONE);
//        mUnfollow.setVisibility(View.VISIBLE);
    }

    @SuppressLint("LongLogTag")
    private void setUnfollowing(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        holder.follow.setVisibility(View.VISIBLE);
//        mUnfollow.setVisibility(View.GONE);
    }

    @SuppressLint("LongLogTag")
    private void isFollowing(Video video, final ViewHolder holder) {
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing(holder);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(mContext.getString(R.string.field_user_id)).equalTo(video.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                        setFollowing(holder);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @SuppressLint("LongLogTag")
    private void setLikes(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        holder.heart_red.setVisibility(View.GONE);
        holder.heart_white.setVisibility(View.VISIBLE);
    }

    @SuppressLint("LongLogTag")
    private void setUnlike(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        holder.heart_red.setVisibility(View.GONE);
        holder.heart_white.setVisibility(View.VISIBLE);
    }
    @SuppressLint("LongLogTag")
    private void isLiking(final ViewHolder holder) {
        Log.d(TAG, "isFollowing: checking if following this users.");

        setUnlike(holder);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_videos))
                .child(holder.video.getUser_id())
                .child(holder.video.getVideo_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    if (singleSnapshot.getValue(Like.class).getUser_id()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        setLikes(holder);
//                        Toast.makeText(mContext, "find like ", Toast.LENGTH_SHORT).show();
                    }else{
                        setUnlike(holder);
//                        Toast.makeText(mContext, "not find like ", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void removeLike(final ViewHolder mHolder){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_all_videos))
                .child(mHolder.video.getVideo_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    String keyID = singleSnapshot.getKey();

                    //case1: Then user already liked the photo
//                    Toast.makeText(mContext, "like is gonna removed", Toast.LENGTH_SHORT).show();
                    mReference.child(mContext.getString(R.string.db_all_videos))
                            .child(mHolder.video.getVideo_id())
                            .child(mContext.getString(R.string.field_likes))
                            .child(keyID)
                            .removeValue();

                    mReference.child(mContext.getString(R.string.db_videos))
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(mHolder.video.getUser_id())
                            .child(mHolder.video.getVideo_id())
                            .child(mContext.getString(R.string.field_likes))
                            .child(keyID)
                            .removeValue();
                    mReference.child(mContext.getString(R.string.db_notification))
                            .child(mHolder.video.getUser_id())
                            .child(keyID)
                            .removeValue();

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("LongLogTag")
    private void addNewLike(final ViewHolder holder){
        Log.d(TAG , "addNewLike: adding new like");

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(holder.userProfile.getUser_id());

        final Notification notification = new Notification();
        notification.setUser_id(holder.userProfile.getUser_id());
//        notification.setDate_created(holder.photo.getDate_created());
//        notification.setComments(holder.photo.getComments());
        notification.setPost_type(mContext.getString(R.string.video_type));
        notification.setDate_created(getTimestamp());
        notification.setLikes(holder.video.getLikes());
        notification.setPost_id(holder.video.getVideo_id());
        notification.setPost_path(holder.video.getVideo_url());
        notification.setNotification_type(mContext.getString(R.string.field_likes));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    currentUsername = singleSnapshot.getValue(user_account_setting.class).getUsername();

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(users_profile.class).getUsername());

                    notification.setProfile_photo(singleSnapshot.getValue(users_profile.class).getProfile_photo());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        notification.setProfile_photo(holder.userProfile.getProfile_photo());


        mReference.child(mContext.getString(R.string.db_all_videos))
                .child(holder.video.getVideo_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.db_videos))
                .child(holder.video.getUser_id())
                .child(holder.video.getVideo_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.db_notification))
                .child(holder.video.getUser_id())
                .child(newLikeID)
                .setValue(notification);

//        holder.heart.toggleLike();
//        getLikesString(holder);
    }


    private void onClickLike(final ViewHolder holder, final Video mUser) {

        // OnClick follow
        holder.heart_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.heart_white.setVisibility(View.GONE);
                holder.heart_red.setVisibility(View.VISIBLE);
//                getLikesCount(holder);
                isLiking(holder);
                addNewLike(holder);
            }
        });

        // On Click Unfollow
        holder.heart_red.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUser_id());

                holder.heart_white.setVisibility(View.VISIBLE);
                holder.heart_red.setVisibility(View.GONE);
//                getLikesCount(holder);
                removeLike(holder);

            }
        });


    }

    @SuppressLint("LongLogTag")
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

//    public class GestureListener extends GestureDetector.SimpleOnGestureListener{
//
//        ViewHolder mHolder;
//        public GestureListener(ViewHolder holder) {
//            mHolder = holder;
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        @SuppressLint("LongLogTag")
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            Log.d(TAG, "onDoubleTap: double tap detected.");
//
//
//            return true;
//        }
//    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    @SuppressLint("LongLogTag")
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



//
//
//
//    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//    Query query = reference
//            .child(mContext.getString(R.string.db_all_videos))
//            .child(mHolder.videoModel.getVideo_id())
//            .child(mContext.getString(R.string.field_likes));
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(DataSnapshot dataSnapshot) {
//        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//
//
//
//        }
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//        });
//



















