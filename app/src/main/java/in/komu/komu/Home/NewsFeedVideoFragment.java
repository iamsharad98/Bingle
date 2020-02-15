package in.komu.komu.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.eschao.android.widget.elasticlistview.LoadFooter;
import com.eschao.android.widget.elasticlistview.OnLoadListener;
import com.eschao.android.widget.elasticlistview.OnUpdateListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.MainfeedListAdapter;
import in.komu.komu.Utils.MainfeedVideoListAdapter;

public class NewsFeedVideoFragment extends Fragment
//        implements OnUpdateListener, OnLoadListener
{

    private static final String TAG = "NewsFeedVideoFragment";

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


    //vars
    private ArrayList<Video> mPhotos;
    private ArrayList<Video> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
//        private ListView mListView;
    private ListView mListView;
    private MainfeedVideoListAdapter adapter;
    private int resultsCount = 0;
    private ArrayList<user_account_setting> mUserAccountSettings;
    //    private ArrayList<UserStories> mAllUserStories = new ArrayList<>();
//    private JSONArray mMasterStoriesArray;

//    private RecyclerView mRecyclerView;
//    public StoriesRecyclerViewAdapter mStoriesAdapter;

    private TextView text, text2;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed_video, container, false);

                mListView = (ListView) view.findViewById(R.id.listView);
                swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);
//        mListView = (ElasticListView) view.findViewById(R.id.listView);
//        mFollowing = new ArrayList<>();
//        mPhotos = new ArrayList<>();

        text = view.findViewById(R.id.text);
        text2 = view.findViewById(R.id.text2);



        text.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);

        initListViewRefresh();
        getFollowing();

        return view;
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
//        mMasterStoriesArray = new JSONArray(new ArrayList<String>());
//        if(mStoriesAdapter != null){
//            mStoriesAdapter.notifyDataSetChanged();
//        }
//        if(mRecyclerView != null){
//            mRecyclerView.setAdapter(null);
//        }
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();
        mPaginatedPhotos = new ArrayList<>();
        mUserAccountSettings = new ArrayList<>();
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        clearAll();
        //also add your own id to the list
        mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getActivity().getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                ;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getFollowing: found user: " + singleSnapshot
                            .child(getString(R.string.field_user_id)).getValue());

                    mFollowing.add(singleSnapshot
                            .child(getString(R.string.field_user_id)).getValue().toString());
                }

                getPhotos();
//                getMyUserAccountSettings();
//                getFriendsAccountSettings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");

        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getActivity().getString(R.string.db_videos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i))
                    ;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Video newVideo = new Video();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

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
                    if(count >= mFollowing.size() - 1){
                        //display the photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: query cancelled.");
                }
            });

        }
    }

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
//
                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                    resultsCount++;
                    Log.d(TAG, "displayPhotos: adding a photo to paginated list: " + mPhotos.get(i).getVideo_id());
                }

                adapter = new MainfeedVideoListAdapter(getActivity(), R.layout.snippet_feed_videopost_section, mPaginatedPhotos);
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

                if (mPhotos.size() == 0 ){
                    text.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.VISIBLE);
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