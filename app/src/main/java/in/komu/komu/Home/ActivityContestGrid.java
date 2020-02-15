package in.komu.komu.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.R;
import in.komu.komu.Utils.GridDiscoverImageAdapter;
import in.komu.komu.Utils.GridImageAdapter;
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.MainfeedVideoListAdapter;
import in.komu.komu.Utils.ViewVideoCommentFragment;

public class ActivityContestGrid extends AppCompatActivity
//        implements OnUpdateListener, OnLoadListener
{

    private static final String TAG = "ActivityContestGrid";

//    @Override
//    public void onUpdate() {
//        Log.d(TAG, "ElasticListView: updating list view...");
//        getFollowing();
//    }
//
//    @Override
//    public void onLoad() {
//        Log.d(TAG, "ElasticListView: loading...");
//
//        // Notify load is done
//        mListView.notifyLoaded();
//    }


    public void onCommentThreadSelected(Video video, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewVideoCommentFragment fragment  = new ViewVideoCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.videos), video);
        args.putString(getString(R.string.home_activity), callingActivity);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    //vars
    private String extras;
    private String contestType;
    private int ACTIVITY_NUM = 2;
    private int NUM_GRID_COLUMNS = 3;
    //vars
    private ArrayList<Video> mPhotos;
    private ArrayList<Video> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
    //        private ListView mListView;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridDiscoverImageAdapter adapter;
    private int resultsCount = 0;
    private ArrayList<user_account_setting> mUserAccountSettings;
    private Context mContext;
    private TextView contestCategory;
    private RelativeLayout mRelativeLayout;
    private FrameLayout mFrameLayout;
    private TextView textNoVideos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_grid);
        mListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.pullToRefresh);

        contestCategory = findViewById(R.id.contestCategory);
        contestType = getImage();
        mContext = ActivityContestGrid.this;

        mRelativeLayout = findViewById(R.id.relLayout1);
        mFrameLayout = findViewById(R.id.container);
        textNoVideos = findViewById(R.id.noVideosText);
        textNoVideos.setVisibility(View.GONE);

        contestCategory.setText("#"+ contestType);

//        Toast.makeText(mContext, "New Activity " + getImage(), Toast.LENGTH_SHORT).show();
        initListViewRefresh();
        getFollowing();


//        setupGridView();
    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    private String getImage(){

        Intent intent;
        intent = getIntent();
        extras = intent.getStringExtra(getString(R.string.contest_type));

        return extras;

    }

    private void initListViewRefresh(){
//        mListView.setHorizontalFadingEdgeEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
//        mListView.enableLoadFooter(true)
//                .getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
//        mListView.setOnUpdateListener(this)
//                .setOnLoadListener(this);
//        mListView.requestUpdate();
    }

    private void clearAll(){
        if(mFollowing != null){
            mFollowing.clear();
        }
        if(mPhotos != null){
            mPhotos.clear();
            if(adapter != null){
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        }

        if(mUserAccountSettings != null){
            mUserAccountSettings.clear();
        }
        if(mPaginatedPhotos != null){
            mPaginatedPhotos.clear();
        }

        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
        mUserAccountSettings = new ArrayList<>();
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");


        clearAll();
        //also add your own id to the list
//        mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.db_contest))
                .child(contestType)
                .child(getString(R.string.db_videos))
                ;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                            .getValue());
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

//                    mFollowing.add(objectMap.get(getString(R.string.user_id)).toString());
                    mFollowing.add(singleSnapshot
                            .child(getString(R.string.field_user_id)).getValue().toString());

//                    singleSnapshot
//                            .child(getString(R.string.field_user_id)).getValue().toString()
                }

                getPhotos();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");

//        for(int i = 0; i < mFollowing.size(); i++){
//            Toast.makeText(mContext, "size" + mFollowing.size(), Toast.LENGTH_SHORT).show();
//            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getString(R.string.db_contest))
                    .child(contestType)
                    .child(getString(R.string.db_videos))
                    ;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Video newVideo = new Video();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

//                        Toast.makeText(mContext, "Category" +
//                                objectMap.get(getString(R.string.field_category)).toString(),
//                                Toast.LENGTH_SHORT).show();

                        newVideo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        newVideo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        newVideo.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
                        newVideo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        newVideo.setTimestamp(objectMap.get(getString(R.string.field_timestamp)).toString());
                        newVideo.setVideo_url(objectMap.get(getString(R.string.field_video_url)).toString());
                        newVideo.setThumb_url(objectMap.get(getString(R.string.field_thumbPath)).toString());
                        newVideo.setCategory(objectMap.get(getString(R.string.field_category)).toString());
                        newVideo.setDuration(objectMap.get(getString(R.string.field_video_duration)).toString());
                        newVideo.setViews(objectMap.get(getString(R.string.field_views)).toString());

                        Log.d(TAG, "getPhotos: photo: " + newVideo.getVideo_id());
                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Map<String, Object> object_map = (HashMap<String, Object>) dSnapshot.getValue();
                            Comment comment = new Comment();
                            comment.setUser_id(object_map.get(getString(R.string.field_user_id)).toString());
                            comment.setComment(object_map.get(getString(R.string.field_comment)).toString());
                            comment.setDate_created(object_map.get(getString(R.string.field_date_created)).toString());
                            commentsList.add(comment);
                        }
                        newVideo.setComments(commentsList);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }

                        newVideo.setLikes(likesList);
                        mPhotos.add(newVideo);
                    }
//                    if(count >= mFollowing.size() - 1){
//                        //display the photos
//                        displayPhotos();
//                    }
                    displayPhotos();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }
//    }

    private void displayPhotos(){
        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){

            try{

                //sort for newest to oldest
                Collections.sort(mPhotos, new Comparator<Video>() {
                    public int compare(Video o1, Video o2) {
                        return o2.getTimestamp().compareTo(o1.getTimestamp());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mPhotos.size();
                if(iterations > 10){
                    iterations = 10;
                }
                if (mPhotos.size() == 0){
                    textNoVideos.setVisibility(View.VISIBLE);
                }
//
                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getVideo_id());
                }

                adapter = new GridDiscoverImageAdapter(mContext, R.layout.snippet_discover_section, mPaginatedPhotos);
                mListView.setAdapter(adapter);

                // Notify update is done
//                mListView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > resultsCount && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                resultsCount = resultsCount + iterations;
                adapter.notifyDataSetChanged();
            }
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
        }
    }



}
