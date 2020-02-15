package in.komu.komu.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import in.komu.komu.MainActivity;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Notification;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.users_profile;
import in.komu.komu.R;
import in.komu.komu.Utils.CommentListAdapter;
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.NotificationListAdapter;

public class ActivityNotification extends AppCompatActivity {

    private static final String TAG = "ActivityNotification";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    //vars
    private Photo mPhoto;
    private Video mVideo;
    private ArrayList<Notification> mNotification;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationListAdapter adapter;
//    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mBackArrow = (ImageView) findViewById(R.id.backArrow);
        mListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.pullToRefresh);
        mNotification = new ArrayList<>();
//        mAdView = findViewById(R.id.adView);
        mContext = ActivityNotification.this;
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        setupFirebaseAuth();
        setupRefreshList();
    }

    private void setupRefreshList() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }


    private void setupWidgets(){

//        Toast.makeText(mContext, "Item " + mNotification.size(), Toast.LENGTH_SHORT).show();
        Collections.sort(mNotification, new Comparator<Notification>() {
            public int compare(Notification o1, Notification o2) {
                return o2.getDate_created().compareTo(o1.getDate_created());
            }
        });

        adapter = new NotificationListAdapter(mContext,
                R.layout.snippet_notification_item, mNotification);
        mListView.setAdapter(adapter);

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                Intent intent = new Intent(ActivityNotification.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }


           /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.child(mContext.getString(R.string.db_notification))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                            Query query = myRef
                                    .child(mContext.getString(R.string.db_notification))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                    .orderByChild(mContext.getString(R.string.field_user_id))
//                                    .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                .child(dataSnapshot.getKey())
//                                .orderByChild(mContext.getString(R.string.field_post_type))
//                                .equalTo(mContext.getString(R.string.photo_type));
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        mNotification.clear();

                                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//                                        Toast.makeText(ActivityNotification.this, "Item " + dataSnapshot.getValue(), Toast.LENGTH_SHORT).show();

                                        Notification notification = new Notification();
                                        notification.setPost_path(objectMap.get(mContext.getString(R.string.field_post_path)).toString());
                                        notification.setPost_id(objectMap.get(mContext.getString(R.string.field_post_id)).toString());
                                        notification.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                        notification.setPost_type(objectMap.get(mContext.getString(R.string.field_post_type)).toString());
                                        notification.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                        notification.setNotification_type(objectMap.get(mContext.getString(R.string.field_notification_type)).toString());
                                        mNotification.add(notification);
                                    }
                                    setupWidgets();


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: query cancelled.");
                                }
                            });



                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}







// If Post is Video Type

//                        Query query2 = myRef
//                                .child(mContext.getString(R.string.db_notification))
//                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                .child(dataSnapshot.getKey())
//                                .orderByChild(mContext.getString(R.string.field_post_type))
//                                .equalTo(mContext.getString(R.string.video_type));
//                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
//
//                                    Video video = new Video();
//                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                                    video.setCaption(objectMap.get(mContext.getString(R.string.field_caption)).toString());
//                                    video.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
//                                    video.setVideo_id(objectMap.get(mContext.getString(R.string.field_video_id)).toString());
//                                    video.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
//                                    video.setTimestamp(objectMap.get(mContext.getString(R.string.field_timestamp)).toString());
//                                    video.setVideo_url(objectMap.get(mContext.getString(R.string.field_video_url)).toString());
//                                    video.setCategory(objectMap.get(mContext.getString(R.string.field_category)).toString());
//                                    video.setThumb_url(objectMap.get(mContext.getString(R.string.field_thumbPath)).toString());
//                                    video.setDuration(objectMap.get(mContext.getString(R.string.field_duration)).toString());
//                                    video.setViews(objectMap.get(mContext.getString(R.string.field_views)).toString());
//                                    video.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
//
//                                    mNotification.clear();
//                                    final Notification notification = new Notification();
//                                    notification.setPost_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());
//                                    notification.setPost_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
//                                    notification.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
//
//
//                                    notification.setPost_type(mContext.getString(R.string.photo_type));
//                                    mNotification.add(notification);
//
//
//
//                                    List<Comment> commentList = new ArrayList<Comment>();
//                                    for (DataSnapshot dSnapshot : singleSnapshot
//                                            .child(mContext.getString(R.string.field_comments)).getChildren()){
//                                        Comment comment = new Comment();
//                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                                        commentList.add(comment);
//                                    }
////                                    notification.setComments(commentList);
//
//                                    List<Like> likesList = new ArrayList<Like>();
//                                    for (DataSnapshot dSnapshot : singleSnapshot
//                                            .child(getString(R.string.field_likes)).getChildren()){
//                                        Like like = new Like();
//                                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                                        likesList.add(like);
//                                    }
//                                    video.setLikes(likesList);
//                                    mVideo = video;
//                                    setupWidgets();
//
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.d(TAG, "onCancelled: query cancelled.");
//                            }
//                        });





















