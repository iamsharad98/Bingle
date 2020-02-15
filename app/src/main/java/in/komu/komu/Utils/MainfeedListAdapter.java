package in.komu.komu.Utils;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import in.komu.komu.Home.OpenPicNewsfeed;
import in.komu.komu.MainActivity;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Notification;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.Models.users_profile;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;


public class MainfeedListAdapter extends ArrayAdapter<Photo> {

    public interface OnLoadMoreItemsListener{
        void onLoadMoreItems();
    }
    OnLoadMoreItemsListener mOnLoadMoreItemsListener;

    private static final String TAG = "MainFeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private int mLikesCount;
    private StringBuilder mUserStringBuilder;
    private List<Like> likes;
    private DatabaseReference mRef1, mRef2, mRef3;



    public MainfeedListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Photo> objects) {
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
        CircleImageView mprofileImage;
        TextView username, dateStamp, caption, likeCount, commentCount;
        SquareImageView postImage;
        ImageView heartRed, heartWhite, comment, shareIcon, optionPost, miniRedHeart, miniComment;

        ElasticListView listView;

        users_profile userProfile = new users_profile();
        user_account_setting accountSetting  = new user_account_setting();
        StringBuilder users;
        boolean likeByCurrentUser;
        Heart heart;
//        GestureDetector detector;
        Photo photo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder();

            holder.mprofileImage =  convertView.findViewById(R.id.profile_photo);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.postImage = (SquareImageView) convertView.findViewById(R.id.postImage);
            holder.heartRed = (ImageView) convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite = (ImageView) convertView.findViewById(R.id.image_heart);
            holder.comment = (ImageView) convertView.findViewById(R.id.ic_comment);
            holder.caption = (TextView) convertView.findViewById(R.id.imgCaption);
            holder.dateStamp =  convertView.findViewById(R.id.dateStamp);
            holder.likeCount =  convertView.findViewById(R.id.likeCount);
            holder.commentCount =  convertView.findViewById(R.id.commentCount);
            holder.optionPost =  convertView.findViewById(R.id.optionPost);
            holder.shareIcon =  convertView.findViewById(R.id.ic_share);
            holder.listView = convertView.findViewById(R.id.listView);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        mRef1 = FirebaseDatabase.getInstance().getReference()
        .child(mContext.getString(R.string.db_user_photos))
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        ;
        mRef1.keepSynced(true);



        holder.photo = getItem(position);

        //Share method
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(getItem(position));
            }
        });
//        holder.detector = new GestureDetector(mContext, new GestureListener(holder));
//        holder.users = new StringBuilder();
//        holder.heart = new Heart(holder.heartWhite, holder.heartRed);

//      Method for likes and unlike
//        getLikesCount(holder);
        onClickLike(holder, getItem(position));
        isLiking(holder);

        //get the current users username (need for checking likes string)
        getCurrentUsername();

        //get likes string
//        getLikesString(holder);

        //set the caption
        holder.caption.setText(getItem(position).getCaption());

        //set the text like
//        Toast.makeText(mContext, "likes "+ getItem(position).getLikes(), Toast.LENGTH_SHORT).show();
        likes = getItem(position).getLikes();
//        Toast.makeText(mContext, "Likes " + likes.size(), Toast.LENGTH_SHORT).show();

        if ((null == likes) && (likes.isEmpty()) ) {
            holder.likeCount.setText(String.valueOf(0));

        }else{
            holder.likeCount.setText(String.valueOf(likes.size()));
        }

        //set the comment
        List<Comment> comments = getItem(position).getComments();
        holder.commentCount.setText(String.valueOf(comments.size()));
//        Toast.makeText(mContext, "Comments" + holder.commentCount, Toast.LENGTH_SHORT).show();
        holder.commentCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: loading comment thread for " + getItem(position).getPhoto_id());

                ((MainActivity)mContext).onCommentThreadSelected(getItem(position),
                        mContext.getString(R.string.newsfeed_photo) );

                //going to need to do something else?
                ((MainActivity)mContext).hideLayout();

            }
        });

        //set the time it was posted
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.dateStamp.setText(timestampDifference + " DAYS AGO");
        }else{
            holder.dateStamp.setText("TODAY");
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

                    currentUsername = singleSnapshot.getValue(user_account_setting.class).getUsername();

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(users_profile.class).getUsername());

                    holder.username.setText(singleSnapshot.getValue(users_profile.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.userProfile.getUsername());

//                            Toast.makeText(mContext, "User Id" + holder.accountSetting.getUser_id().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, activity_profile.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.accountSetting);
                            mContext.startActivity(intent);
                        }
                    });

                    Glide
                            .with(getContext().getApplicationContext())
                            .load(singleSnapshot.getValue(users_profile.class).getProfile_photo())
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.loading_image))

                            .into(holder.mprofileImage);

                    holder.mprofileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: navigating to profile of: " +
                                    holder.userProfile.getUsername());

//                            Toast.makeText(mContext, "User Id" + holder.accountSetting.getUser_id().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, activity_profile.class);
                            intent.putExtra(mContext.getString(R.string.calling_activity),
                                    mContext.getString(R.string.home_activity));
                            intent.putExtra(mContext.getString(R.string.intent_user), holder.accountSetting);
                            mContext.startActivity(intent);
                        }
                    });

                    holder.userProfile = singleSnapshot.getValue(users_profile.class);
                    holder.accountSetting = singleSnapshot.getValue(user_account_setting.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MainActivity)mContext).onCommentThreadSelected(getItem(position),
                                    mContext.getString(R.string.home_activity));

                            //another thing?
                            ((MainActivity)mContext).hideLayout();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set the post image
        Glide
                .with(getContext())
                .load(getItem(position).getImage_path())
                .apply(new RequestOptions()
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.loading_image))

                .into(holder.postImage);

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OpenPicNewsfeed.class);
                intent.putExtra(getContext().getString(R.string.post_image), getItem(position).getImage_path());
                ActivityOptions.makeCustomAnimation(mContext, R.anim.fade_in, R.anim.fade_out);
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
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: found user: " +
                    singleSnapshot.getValue(users_profile.class).getUsername());


                    holder.userProfile = singleSnapshot.getValue(users_profile.class);
//                    holder.accountSetting = singleSnapshot.getValue(user_account_setting.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(reachedEndOfList(position)){
            loadMoreData();
        }
        return convertView;
    }

    // Share Method
    @JavascriptInterface
    public void sendMessage(Photo photo) {
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

    private void onClickLike(final ViewHolder holder, final Photo mUser) {

        // OnClick follow
        holder.heartWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.heartWhite.setVisibility(View.GONE);
                holder.heartRed.setVisibility(View.VISIBLE);
//                likes = mUser.getLikes();
//
//                if ((null == likes) && (likes.isEmpty()) ) {
//                    holder.likeCount.setText(String.valueOf(0));
//
//                }else{
//                    holder.likeCount.setText(String.valueOf(likes.size()));
//                }

//                getLikesCount(holder);
                addNewLike(holder);
            }
        });

        // On Click Unfollow
        holder.heartRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUser_id());

                holder.heartWhite.setVisibility(View.VISIBLE);
                holder.heartRed.setVisibility(View.GONE);
//                likes = mUser.getLikes();
//
//                if ((null == likes) && (likes.isEmpty()) ) {
//                    holder.likeCount.setText(String.valueOf(0));
//
//                }else{
//                    holder.likeCount.setText(String.valueOf(likes.size()));
//                }
//                getLikesCount(holder);
                removeLike(holder);

            }
        });


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

    private void getCurrentUsername(){
        Log.d(TAG, "getCurrentUsername: retrieving user account settings");
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
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void getLikesCount(final ViewHolder holder){
//        Log.d(TAG, "getLikesString: getting likes string");
//
//        Log.d(TAG, "getLikesString: photo id: " + holder.photo.getPhoto_id());
//            mLikesCount = 0;
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(mContext.getString(R.string.db_photos))
//                .child(holder.photo.getPhoto_id())
//                .child(mContext.getString(R.string.field_likes));
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//                    mLikesCount++;
//                }
//                holder.likeCount.setText(String.valueOf(mLikesCount));
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void setLikes(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        holder.heartWhite.setVisibility(View.GONE);
        holder.heartRed.setVisibility(View.VISIBLE);
    }

    private void setUnlike(ViewHolder holder) {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        holder.heartRed.setVisibility(View.GONE);
        holder.heartWhite.setVisibility(View.VISIBLE);
    }

    private void isLiking(final ViewHolder holder) {
        Log.d(TAG, "isFollowing: checking if following this users.");

        setUnlike(holder);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                    if (singleSnapshot.getValue(Like.class).getUser_id()
                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        setLikes(holder);
                    }else{
                        setUnlike(holder);
                    }
                }


//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//                Query query = reference.child(mContext.getString(R.string.db_following))
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        .orderByChild(mContext.getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                            Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());
//
////                            setFollowing();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void removeLike(final ViewHolder mHolder){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(mContext.getString(R.string.db_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes));
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        String keyID = singleSnapshot.getKey();

                        //case1: Then user already liked the photo
//                            Toast.makeText(mContext, "like is gonna removed", Toast.LENGTH_SHORT).show();
                            mReference.child(mContext.getString(R.string.db_photos))
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();

                            mReference.child(mContext.getString(R.string.db_user_photos))
//                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mHolder.photo.getUser_id())
                                    .child(mHolder.photo.getPhoto_id())
                                    .child(mContext.getString(R.string.field_likes))
                                    .child(keyID)
                                    .removeValue();
                            mReference.child(mContext.getString(R.string.db_notification))
                                    .child(mHolder.photo.getUser_id())
                                    .child(keyID)
                                    .removeValue();

                        }

//                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

    }

    private void addNewLike(final ViewHolder holder){
        Log.d(TAG, "addNewLike: adding new like");

        String newLikeID = mReference.push().getKey();
        Like like = new Like();
        like.setUser_id(holder.userProfile.getUser_id());

        final Notification notification = new Notification();
        notification.setUser_id(holder.userProfile.getUser_id());
//        notification.setDate_created(holder.photo.getDate_created());
//        notification.setComments(holder.photo.getComments());
        notification.setPost_type(mContext.getString(R.string.photo_type));
        notification.setDate_created(getTimestamp());
        notification.setLikes(holder.photo.getLikes());
        notification.setPost_id(holder.photo.getPhoto_id());
        notification.setPost_path(holder.photo.getImage_path());
        notification.setNotification_type(mContext.getString(R.string.field_likes));

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(mContext.getString(R.string.db_users_profile))
//                .orderByChild(mContext.getString(R.string.field_user_id))
//                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//
//                    Log.d(TAG, "onDataChange: found user: "
//                            + singleSnapshot.getValue(users_profile.class).getUsername());
//
//                    notification.setProfile_photo(singleSnapshot.getValue(users_profile.class).getProfile_photo());
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        notification.setProfile_photo(holder.userProfile.getProfile_photo());

        mReference.child(mContext.getString(R.string.db_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.db_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID)
                .setValue(like);

        mReference.child(mContext.getString(R.string.db_notification))
                .child(holder.photo.getUser_id())
                .child(newLikeID)
                .setValue(notification);
//        Toast.makeText(mContext, "notification " + holder.userProfile.getUser_id(), Toast.LENGTH_SHORT).show();

//        holder.heart.toggleLike();
//        getLikesString(holder);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }




    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimestampDifference(Photo photo){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }

}



//  DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                    Query query = reference
//                            .child(mContext.getString(R.string.db_users_profile))
//                            .orderByChild(mContext.getString(R.string.field_user_id))
//                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());
//                    query.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//
//
//
//
//                            }
//
//                            //setup likes string
//                            setupLikesString(holder);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });


//       singleSnapshot.getValue(users_profile.class).getUsername());
//
//                                mUserStringBuilder.append(singleSnapshot.getValue(users_profile.class).getUsername());
//                                mUserStringBuilder.append(",");
//                                String[] splitUsers = holder.users.toString().split(",");
//
//                                if(mUserStringBuilder.toString().contains(currentUsername + ",")){//mitch, mitchell.tabian
//                                    holder.likeByCurrentUser = true;
//                                }else{
//                                    holder.likeByCurrentUser = false;
//                                }
//
//                                mLikesCount = 0;
//                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//                                Query query = reference.child(mContext.getString(R.string.db_photos))
//                                        .child(holder.photo.getPhoto_id())
//                                        .child(mContext.getString(R.string.field_likes));
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                                            Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
//                                            mLikesCount++;
//                                        }
//
//                                        holder.likeCount.setText(String.valueOf(mLikesCount));
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//
////                                holder.likeCount.setText(String.valueOf(splitUsers.length));




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
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//
//            Log.d(TAG, "onDoubleTap: double tap detected.");
//
//            Log.d(TAG, "onDoubleTap: clicked on photo: " + mHolder.photo.getPhoto_id());
//
//
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//            Query query = reference
//                    .child(mContext.getString(R.string.db_photos))
//                    .child(mHolder.photo.getPhoto_id())
//                    .child(mContext.getString(R.string.field_likes));
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
//
//                        String keyID = singleSnapshot.getKey();
//
//                        //case1: Then user already liked the photo
//                        if(mHolder.likeByCurrentUser
//                                && singleSnapshot.getValue(Like.class).getUser_id()
//                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                ){
//
//                            Toast.makeText(mContext, "like is gonna removed", Toast.LENGTH_SHORT).show();
//                            mReference.child(mContext.getString(R.string.db_photos))
//                                    .child(mHolder.photo.getPhoto_id())
//                                    .child(mContext.getString(R.string.field_likes))
//                                    .child(keyID)
//                                    .removeValue();
//
//                            mReference.child(mContext.getString(R.string.db_user_photos))
////                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                    .child(mHolder.photo.getUser_id())
//                                    .child(mHolder.photo.getPhoto_id())
//                                    .child(mContext.getString(R.string.field_likes))
//                                    .child(keyID)
//                                    .removeValue();
//                            mReference.child(mContext.getString(R.string.db_notification))
//                                    .child(mHolder.photo.getUser_id())
//                                    .child(keyID)
//                                    .removeValue();
//
//                            mHolder.heart.toggleLike();
////                            getLikesString(mHolder);
//                        }
//                        //case2: The user has not liked the photo
//                        else if(!mHolder.likeByCurrentUser){
//                            //add new like
//                            addNewLike(mHolder);
//                            setupLikesString(mHolder);
////                            getLikesString(mHolder);
//
//                            break;
//                        }
//                    }
//                    if(!dataSnapshot.exists()){
//                        //add new like
////                        getLikesString(mHolder);
//
//                        addNewLike(mHolder);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//
//            return true;
//        }
//    }


//    @SuppressLint("ClickableViewAccessibility")
//    private void setupLikesString(final ViewHolder holder){
////        Log.d(TAG, "setupLikesString: likes string:" + holder.likesString);
//
//        Log.d(TAG, "setupLikesString: photo id: " + holder.photo.getPhoto_id());
//        if(holder.likeByCurrentUser){
//            Log.d(TAG, "setupLikesString: photo is liked by current user");
//            holder.heartWhite.setVisibility(View.GONE);
//            holder.heartRed.setVisibility(View.VISIBLE);
//            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
////                    holder.likeCount.setText(String.valueOf(splitUsers.length));
//
//                    return holder.detector.onTouchEvent(event);
//                }
//            });
//
//        }else{
//            Log.d(TAG, "setupLikesString: photo is not liked by current user");
//            holder.heartWhite.setVisibility(View.VISIBLE);
//            holder.heartRed.setVisibility(View.GONE);
//            mLikesCount = 0;
//            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return holder.detector.onTouchEvent(event);
//                }
//            });
//        }
//    }


















