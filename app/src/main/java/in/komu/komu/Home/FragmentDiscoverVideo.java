package in.komu.komu.Home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

//import com.felipecsl.asymmetricgridview.library.Utils;
//import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
//import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
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
import in.komu.komu.R;
import in.komu.komu.Utils.GridDiscoverImageAdapter;
import in.komu.komu.Utils.Like;

public class FragmentDiscoverVideo extends Fragment {
    private static final String TAG = "FragmentDiscoverVideo";

    //vars
    private int ACTIVITY_NUM = 5;
    private int NUM_GRID_COLUMNS = 3;

//    public interface OnGridImageSelectedListener{
//        void onGridImageSelected(Video video, int activityNumber);
//    }
//    FragmentDiscoverVideo.OnGridImageSelectedListener mOnGridImageSelectedListener;

    //widgets
    private GridView gridView;
    private GridDiscoverImageAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        gridView = view.findViewById(R.id.customGridView);
        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);



        setupGridView();
        setupRefreshLayout();

        return view;
    }

    private void setupRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gridView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }


    public void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up video grid.");

        final ArrayList<Video> videos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_all_videos));
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

                    Video video = new Video();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        video.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        video.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        video.setVideo_id(objectMap.get(getString(R.string.field_video_id)).toString());
                        video.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        video.setTimestamp(objectMap.get(getString(R.string.field_timestamp)).toString());
                        video.setVideo_url(objectMap.get(getString(R.string.field_video_url)).toString());
                        video.setThumb_url(objectMap.get(getString(R.string.field_thumbPath)).toString());
                        video.setCategory(objectMap.get(getString(R.string.field_category)).toString());
                        video.setDuration(objectMap.get(getString(R.string.field_video_duration)).toString());
                        video.setViews(objectMap.get(getString(R.string.field_views)).toString());



                        List<Comment> mComments = new ArrayList<Comment>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getActivity().getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            mComments.add(comment);
                        }

                        video.setComments(mComments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }

                        video.setLikes(likesList);
                        videos.add(video);

                    }catch (NullPointerException e ){
                        Log.d(TAG, "onDataChange: NullPointerException " + e.getMessage());
                    }


                    //setup our image grid
                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                    gridView.setColumnWidth(imageWidth);
//                    gridView.setRequestedColumnWidth(Utils.dpToPx(getContext(), 120));

//                    Toast.makeText(mContext, "photos are" + photos, Toast.LENGTH_SHORT).show();
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                            Toast.makeText(getContext(), "photo is "+ position, Toast.LENGTH_SHORT).show();
//                            mOnGridImageSelectedListener.onGridImageSelected(videos.get(position), ACTIVITY_NUM);

                        }
                    });

                    Collections.sort(videos, new Comparator<Video>() {
                        public int compare(Video o1, Video o2) {
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        }
                    });

                    ArrayList<Video> videoUrls = new ArrayList<Video>();
                    for(int i = 0; i < videos.size(); i++){
                        videoUrls.add(videos.get(i));
                    }
                    adapter = new GridDiscoverImageAdapter(getActivity(),
                            R.layout.snippet_discover_section,
                             videoUrls);


                    // initialize your items array
//                    AsymmetricGridViewAdapter asymmetricAdapter =
//                            new AsymmetricGridViewAdapter<>(getActivity(), gridView, adapter);
                    gridView.setAdapter(adapter);
//                    gridView.setAllowReordering(true);
//
//                    gridView.isAllowReordering();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }


        });

    }

//    @Override
//    public void onAttach(Context context) {
//        try{
//            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
//        }catch (ClassCastException e){
//            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
//        }
//        super.onAttach(context);
//    }

}
