package in.komu.komu.UserProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.R;
import in.komu.komu.Utils.GridImageAdapter;
import in.komu.komu.Utils.Like;

public class FragmentViewGridPhoto extends Fragment implements ViewProfileFragment.OnViewPagerSelectedListener{
    private static final String TAG = "FragmentViewGridPhoto";

    public FragmentViewGridPhoto(){
        super();
        setArguments(new Bundle());
    }
    private  user_account_setting mUser;

    //vars
    private int ACTIVITY_NUM = 7;
    private int NUM_GRID_COLUMNS = 3;

    @Override
    public void OnViewPagerSelected(user_account_setting user) {
        mUser = user;
    }


    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo, int activityNumber);
    }
    FragmentViewGridPhoto.OnGridImageSelectedListener mOnGridImageSelectedListener;


    //widgets
    private GridView gridView;
    private Context mContext;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_photos_grid, container, false);

        gridView = view.findViewById(R.id.gridViewPhoto);
        mContext = getActivity();

//        mUser = getUserFromBundle();
//        init();

        return view;
    }




    //Init Function
    private void init() {

        //get the users profile photos

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.db_user_photos))
                .child(mUser.getUser_id());
        Toast.makeText(mContext, "user id" + mUser.getUsername(), Toast.LENGTH_SHORT).show();
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Photo> photos = new ArrayList<Photo>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_comments)).getChildren()) {
                        Comment comment = new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }

                    photo.setComments(comments);

                    List<Like> likesList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot
                            .child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likesList.add(like);
                    }
                    photo.setLikes(likesList);
                    photos.add(photo);
                    setupImageGrid(photos);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
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


    private void setupImageGrid(final ArrayList<Photo> photos){
        //setup our image grid
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        ArrayList<String> imgUrls = new ArrayList<String>();
        for(int i = 0; i < photos.size(); i++){
            imgUrls.add(photos.get(i).getImage_path());
        }
        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
                "", imgUrls);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
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










