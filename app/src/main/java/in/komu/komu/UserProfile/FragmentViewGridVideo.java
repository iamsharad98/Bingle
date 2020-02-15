package in.komu.komu.UserProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.ProfileVideoListAdapter;

public class FragmentViewGridVideo extends Fragment  implements OnUpdateListener, OnLoadListener, ViewProfileFragment.OnViewPagerSelectedListener{

    private static final String TAG = "FragmentGridVideo";
    private int ACTIVITY_NUM = 5;
    private user_account_setting mUser;

    @Override
    public void OnViewPagerSelected(user_account_setting user) {
        mUser = user;
    }


    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener(Video video);
    }
    FragmentViewGridVideo.OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public FragmentViewGridVideo(){
        super();
        setArguments(new Bundle());
    }


    @Override
    public void onLoad() {
        getFollowing();
    }

    @Override
    public void onUpdate() {
        Log.d(TAG, "ElasticListView: loading...");

        // Notify load is done
        mListView.notifyLoaded();
    }


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //vars
    private ArrayList<Video> mVideo;
    private ArrayList<Video> mPaginatedVideos;
    private ArrayList<String> mFollowing;
    private int recursionIterator = 0;
    //        private ListView mListView;
    private ElasticListView mListView;
    private ProfileVideoListAdapter adapter;
    private int resultsCount = 0;
    private ArrayList<user_account_setting> mUserAccountSettings;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_videos_grid, container, false);

        mListView = (ElasticListView) view.findViewById(R.id.listView);
//        mListView = (ElasticListView) view.findViewById(R.id.listView);
//        mFollowing = new ArrayList<>();
//        mPhotos = new ArrayList<>();

//        mUser = getUserFromBundle();


        if(mUser!= null) {
            Toast.makeText(getContext(), "User id " + mUser.getUsername(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "mUser is null", Toast.LENGTH_SHORT).show();
        }
        initListViewRefresh();
        getFollowing();
        return view;
    }

    private user_account_setting getUserFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: Get user");

        Bundle bundle = this.getArguments();
        if (bundle!= null){
            return bundle.getParcelable("mUser");
        }else{

            return null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnCommentThreadSelectedListener = (FragmentViewGridVideo.OnCommentThreadSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    private void initListViewRefresh(){
        mListView.setHorizontalFadingEdgeEnabled(true);
        mListView.setAdapter(adapter);
        mListView.enableLoadFooter(true)
                .getLoadFooter().setLoadAction(LoadFooter.LoadAction.RELEASE_TO_LOAD);
        mListView.setOnUpdateListener(this)
                .setOnLoadListener(this);
//        mListView.requestUpdate();
    }

    private void clearAll(){
        if(mFollowing != null){
            mFollowing.clear();
        }
        if(mVideo != null){
            mVideo.clear();
            if(adapter != null){
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        }
        if(mUserAccountSettings != null){
            mUserAccountSettings.clear();
        }
        if(mPaginatedVideos != null){
            mPaginatedVideos.clear();
        }
//        mMasterStoriesArray = new JSONArray(new ArrayList<String>());
//        if(mStoriesAdapter != null){
//            mStoriesAdapter.notifyDataSetChanged();
//        }
//        if(mRecyclerView != null){
//            mRecyclerView.setAdapter(null);
//        }
        mFollowing = new ArrayList<>();
        mVideo = new ArrayList<>();
        mPaginatedVideos = new ArrayList<>();
        mUserAccountSettings = new ArrayList<>();
    }


    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");

        clearAll();
        //also add your own id to the list
        mFollowing.add(mUser.getUser_id());

//        Toast.makeText(getActivity(), "User " + mUser.getUser_id(), Toast.LENGTH_SHORT).show();
        getPhotos();

    }

    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting list of photos");

        for(int i = 0; i < mFollowing.size(); i++){
            final int count = i;
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child(getActivity().getString(R.string.db_videos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mUser.getUser_id())
                    ;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                        Video video = new Video();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        video.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        video.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        video.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
                        video.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        video.setTimestamp(objectMap.get(getString(R.string.field_timestamp)).toString());
                        video.setVideo_url(objectMap.get(getString(R.string.field_video_url)).toString());
                        video.setViews(objectMap.get(getString(R.string.field_video_views)).toString());
                        video.setDuration(objectMap.get(getString(R.string.field_video_duration)).toString());

                        Log.d(TAG, "getVideos: video: " + video.getVideo_id());
                        List<Comment> commentsList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getActivity().getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            commentsList.add(comment);
                        }
                        video.setComments(commentsList);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }
                        video.setLikes(likesList);
                        mVideo.add(video);

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
        mPaginatedVideos = new ArrayList<>();
        if(mVideo != null){

            try{

                //sort for newest to oldest
                Collections.sort(mVideo, new Comparator<Video>() {
                    public int compare(Video o1, Video o2) {
                        return o2.getTimestamp().compareTo(o1.getTimestamp());
                    }
                });

                //we want to load 10 at a time. So if there is more than 10, just load 10 to start
                int iterations = mVideo.size();
                if(iterations > 10){
                    iterations = 10;
                }

                resultsCount = 0;
                for(int i = 0; i < iterations; i++){
                    mPaginatedVideos.add(mVideo.get(i));
                    resultsCount++;
                    Log.d(TAG, "displaying Videos: adding a Video to paginated list: " + mVideo.get(i).getVideo_id());
                }

//                Toast.makeText(getActivity(), "All videso  lsit" + mPaginatedVideos.toString(), Toast.LENGTH_SHORT).show();
                adapter = new ProfileVideoListAdapter(getActivity(), R.layout.layout_view_video_post, mPaginatedVideos);
//                RoughVideoAdapter adapter2;
//                adapter2 = new RoughVideoAdapter(getActivity(), mPaginatedVideos);

                mListView.setAdapter(adapter);

                // Notify update is done
                mListView.notifyUpdated();

            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException:" + e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException:" + e.getMessage() );
            }
        }
    }

    public void displayMoreVideos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mVideo.size() > resultsCount && mVideo.size() > 0){

                int iterations;
                if(mVideo.size() > (resultsCount + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mVideo.size() - resultsCount;
                }

                //add the new photos to the paginated list
                for(int i = resultsCount; i < resultsCount + iterations; i++){
                    mPaginatedVideos.add(mVideo.get(i));
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

//
//       /*
//    ------------------------------------ Firebase ---------------------------------------------
//     */
//
//    /**
//     * Setup the firebase auth object
//     */
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//
//        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        myRef = mFirebaseDatabase.getReference();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//
//
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }


}













////********************When We Want to show in gridview ***********////
//
//    public interface OnGridVideoSelectedListener{
//        void OnGridVideoSelectedListener(Video video, int activityNumber);
//    }
//    FragmentGridVideo.OnGridVideoSelectedListener mOnGridVideoSelectedListener;


//    //widgets
//    private GridView gridView;
//
//    //vars
//    private int NUM_GRID_COLUMNS = 3;
//
//
//
//    @Override
//    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile_videos_grid, container, false);
//        gridView = view.findViewById(R.id.gridViewVideo);
//        setupGridView();
//
//        return view;
//    }
//
//    private void setupGridView() {
//        Log.d(TAG, "setupGridView: Setting up Video grid.");
//
//        final ArrayList<Video> videos = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.db_videos))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//
//                    final Video video = new Video();
//                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                    try {
//                        video.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
//                        video.setTags(objectMap.get(getString(R.string.field_tags)).toString());
//                        video.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
//                        video.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
//                        video.setTimestamp(objectMap.get(getString(R.string.field_date_created)).toString());
//                        video.setVideo_url(objectMap.get(getString(R.string.field_image_path)).toString());
//                        video.setViews(objectMap.get(getString(R.string.field_video_views)).toString());
//                        video.setDuration(objectMap.get(getString(R.string.field_video_duration)).toString());
//
//                        List<Comment> mComments = new ArrayList<Comment>();
//
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child(getActivity().getString(R.string.field_comments)).getChildren()){
//                            Comment comment = new Comment();
//                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                            mComments.add(comment);
//                        }
//
//                        video.setComments(mComments);
//
//                        List<Like> likesList = new ArrayList<Like>();
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child(getString(R.string.field_likes)).getChildren()) {
//                            Like like = new Like();
//                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                            likesList.add(like);
//                        }
//
//                        video.setLikes(likesList);
//                        videos.add(video);
//
//                    }catch (NullPointerException e ){
//                        Log.d(TAG, "onDataChange: NullPointerException " + e.getMessage());
//                    }
//
//
//                    //setup our image grid
//                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
//                    int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//                    gridView.setColumnWidth(imageWidth);
//
//
////                    Toast.makeText(mContext, "photos are" + photos, Toast.LENGTH_SHORT).show();
//                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
////                            Toast.makeText(mContext, "photo is "+ photos.get(position), Toast.LENGTH_SHORT).show();
//                            mOnGridVideoSelectedListener.OnGridVideoSelectedListener(videos.get(position), ACTIVITY_NUM);
//
//                        }
//                    });
//
//                    ArrayList<Video> videoUrls = new ArrayList<Video>();
//                    for(int i = 0; i < videos.size(); i++){
//                        videoUrls.add(videos.get(i));
//                    }
//                    Toast.makeText(getActivity(), "videoUrl" + videoUrls, Toast.LENGTH_SHORT).show();
//                    ProfileVideoListAdapter adapter = new ProfileVideoListAdapter(getActivity(),R.layout.snippet_video_item,
//                            videoUrls);
//                    gridView.setAdapter(adapter);
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//
//
//        });
//
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        try{
//            mOnGridVideoSelectedListener = (OnGridVideoSelectedListener) getActivity();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
//        }
//        super.onAttach(context);
//    }

