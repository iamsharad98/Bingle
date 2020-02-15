package in.komu.komu.Profile;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Photo;
import in.komu.komu.R;
import in.komu.komu.Utils.GridImageAdapter;
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.Permissions;

public class FragmentGridPhoto extends Fragment {
    private static final String TAG = "FragmentGridPhoto";

    //vars
    private int ACTIVITY_NUM = 2;
    private int NUM_GRID_COLUMNS = 3;

    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    FragmentGridPhoto.OnGridImageSelectedListener mOnGridImageSelectedListener;


    //widgets
    private GridView gridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridImageAdapter adapter;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_photos_grid, container, false);

        gridView = view.findViewById(R.id.gridViewPhoto);
        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);

        setupGridView();
        onListRefresh();

        return view;
    }

    private void onListRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gridView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.db_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){

//                    Toast.makeText(getContext(), "Activity " + singleSnapshot, Toast.LENGTH_SHORT).show();
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        List<Comment> mComments = new ArrayList<Comment>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getActivity().getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            mComments.add(comment);
                        }

                        photo.setComments(mComments);

                        List<Like> likesList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
                            Like like = new Like();
                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                            likesList.add(like);
                        }

                        photo.setLikes(likesList);
                        photos.add(photo);

                    }catch (NullPointerException e ){
                        Log.d(TAG, "onDataChange: NullPointerException " + e.getMessage());
                    }


                    //setup our image grid
                    int gridWidth = getResources().getDisplayMetrics().widthPixels;
                    int imageWidth = gridWidth/NUM_GRID_COLUMNS;
                    gridView.setColumnWidth(imageWidth);


//                    Toast.makeText(mContext, "photos are" + photos, Toast.LENGTH_SHORT).show();
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                            Toast.makeText(mContext, "photo is "+ photos.get(position), Toast.LENGTH_SHORT).show();
                            mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);

                        }
                    });

                    ArrayList<String> imgUrls = new ArrayList<String>();
                    for(int i = 0; i < photos.size(); i++){
                        imgUrls.add(photos.get(i).getImage_path());
                    }
                    adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                            "", imgUrls);
                    gridView.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }


        });

    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
        super.onAttach(context);
    }

}
