package in.komu.komu.UserProfile;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.eschao.android.widget.elasticlistview.ElasticListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;
import in.komu.komu.MainActivity;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.userSettings;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.Models.users_profile;
import in.komu.komu.Profile.EditProfileFragment;
import in.komu.komu.Profile.FragmentGridPhoto;
import in.komu.komu.Profile.FragmentGridVideo;
import in.komu.komu.Profile.activity_accountSetting;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Search.SearchActivity;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.GridImageAdapter;
import in.komu.komu.Utils.Like;
import in.komu.komu.Utils.Permissions;
import in.komu.komu.Utils.ProfileVideoListAdapter;
import in.komu.komu.Utils.SectionsPagerAdapter;


public class ViewProfileFragment extends Fragment
//        implements
//        ProfileVideoListAdapter.OnLoadMoreItemsListener
{

    private static final String TAG = "ViewProfileFragment";
//
//    @Override
//    public void onLoadMoreItems() {
//
//        Log.d(TAG, "onLoadMoreItems: displaying more photos");
//        FragmentViewGridVideo fragment = (FragmentViewGridVideo) getChildFragmentManager()
//                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + viewPager.getCurrentItem());
//        if (fragment != null) {
//            fragment.displayMoreVideos();
//        }
//    }

    OnViewPagerSelectedListener mOnViewPagerSelectedListener;

    public interface OnViewPagerSelectedListener{
        void OnViewPagerSelected(user_account_setting mUser);
    }


//    public void setOnViewPager(OnViewPagerSelectedListener mUser) {
//        this.mOnViewPagerSelectedListener = mUser;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnViewPagerSelectedListener = (OnViewPagerSelectedListener) getActivity();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    private int ACTIVITY_NUM = 4;

    // var
    private static int NUM_GRID_COLUMNS = 3;
    private static final int REQUEST_CODE = 2;

    //firebase stuff
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    // Widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, mOrigin, mEditProfile,
            mFollow, mUnfollow, text, text2;
    //private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private ElasticListView listView;
    private Toolbar toolbar;
    private ImageView profileMenu, backArrow;

    //vars
    private user_account_setting mUser;
    private int mFollowersCount;
    private int mFollowingCount;
    private int mPostsCount;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ArrayList<Video> mPaginatedVideos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        // initiate Widgets
        text = view.findViewById(R.id.text);
        text2 = view.findViewById(R.id.text2);

        mDisplayName = view.findViewById(R.id.display_name);
//        mOrigin = view.findViewById(R.id.origin);
        mUsername = view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);
        listView = view.findViewById(R.id.listView);
        toolbar = view.findViewById(R.id.profileToolBar);
        profileMenu = view.findViewById(R.id.profileMenu);
//        mEditProfile = view.findViewById(R.id.textEditProfile);
        backArrow = view.findViewById(R.id.backArrow);
        mFollow = view.findViewById(R.id.follow);
        mUnfollow = view.findViewById(R.id.unfollow);
//        viewPager = view.findViewById(R.id.viewpager_container);
//        tabLayout = view.findViewById(R.id.topTabs);


        mContext = getActivity();

        try {
            mUser = getUserFromBundle();

//            Toast.makeText(mContext, "mUser "+ mUser.getUsername(), Toast.LENGTH_SHORT).show();
//            if (mUser!= null){
////                Bundle args = new Bundle();
////                    args.putParcelable(getString(R.string.intent_user), mUser);
////                    View Profile Fragment
////                    FragmentViewGridPhoto fragment1 = new FragmentViewGridPhoto();
////                    fragment1.setArguments(args);
//                mOnViewPagerSelectedListener.OnViewPagerSelected(mUser);
//
//
////
////                FragmentViewGridVideo fragment2 = new FragmentViewGridVideo();
////                    fragment2.setArguments(args);
////
//
//            }

            init();
        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
            Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }
//        Toast.makeText(mContext, "User " + mUser.getUsername(), Toast.LENGTH_SHORT).show();
//        mOnViewPagerSelectedListener.OnViewPagerSelected(mUser);


//        if (checkPermissionArray(Permissions.PERMISSIONS)) {
////            setupViewPager();
//        } else {
//            verifyPermission(Permissions.PERMISSIONS);
//        }

        // Firebase stuff
        setupFirebaseAuth();
//        setupGridView();
        getFollowingCount();
        getFollowersCount();


        firebaseMethods = new FirebaseMethods(getActivity());

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        // Call function to check if user already follow or not
        isFollowing();

        // Toolbar Setup
        setupToolbar();


        // OnClick follow
        mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following: " + mUser.getUsername());

                mFollow.setVisibility(View.GONE);
                mUnfollow.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .child(getString(R.string.field_user_id))
                        .setValue(mUser.getUser_id());

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_follower))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_user_id))
                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });

        // On Click Unfollow
        mUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing: " + mUser.getUsername());

                mFollow.setVisibility(View.VISIBLE);
                mUnfollow.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_following))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mUser.getUser_id())
                        .removeValue();

                FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.db_follower))
                        .child(mUser.getUser_id())
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();
            }
        });

        // Call the back button method
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i(TAG, "onKey Back listener is working!!!");
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
//                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });


        return view;
    }


    //Init Function
    private void init() {

        //set the profile widgets
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.db_user_account_setting))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mUser.getUser_id());
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(user_account_setting.class)
                            .toString());

                    userSettings settings = new userSettings();
                    settings.setSetting(mUser);
                    settings.setUsers_profile(singleSnapshot.getValue(users_profile.class));
                    setProfileWidgets(settings);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Set the Profile Videos
        setupGridView();


    }


    private void isFollowing() {
        Log.d(TAG, "isFollowing: checking if following this users.");
        setUnfollowing();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild(getString(R.string.field_user_id)).equalTo(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue());

                    setFollowing();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFollowing() {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        mFollow.setVisibility(View.GONE);
        mUnfollow.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        mFollow.setVisibility(View.VISIBLE);
        mUnfollow.setVisibility(View.GONE);
    }

    private void getFollowersCount() {

        mFollowersCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_follower))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
//                Toast.makeText(mContext, "Followers" + mFollowersCount, Toast.LENGTH_SHORT).show();
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowingCount() {

        mFollowersCount = 0;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
//                Toast.makeText(mContext, "Followers" + mFollowingCount, Toast.LENGTH_SHORT).show();

                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public user_account_setting getUserFromBundle() {
        Log.d(TAG, "getUserFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.intent_user));
        } else {
            return null;
        }
    }

    private void setProfileWidgets(userSettings user_settings) {

        users_profile usersProfile = user_settings.getUsers_profile();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(mUser.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user: " +
                            singleSnapshot.getValue(user_account_setting.class).toString());


                    mUsername.setText(singleSnapshot.getValue(users_profile.class).getUsername());
                    mDisplayName.setText(singleSnapshot.getValue(users_profile.class).getDisplay_name());
                    mWebsite.setText(singleSnapshot.getValue(users_profile.class).getWebsite());
                    mDescription.setText(singleSnapshot.getValue(users_profile.class).getAbout());
//                    mOrigin.setText(singleSnapshot.getValue(users_profile.class).getOrigin());

                    Glide
                            .with(getContext())
                            .load(singleSnapshot.getValue(users_profile.class).getProfile_photo())
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_prof))
                            .into(mProfilePhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        Query query2 = reference2
                .child(getString(R.string.db_videos))
                .child(mUser.getUser_id());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Video> videos = new ArrayList<Video>();
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
                    video.setThumb_url(objectMap.get(getString(R.string.field_thumbPath)).toString());

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
                    videos.add(video);
                    //sort for newest to oldest
                    Collections.sort(videos, new Comparator<Video>() {
                        public int compare(Video o1, Video o2) {
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        }
                    });

                    if (videos.size() == 0){
                        text.setVisibility(View.VISIBLE);
                        text2.setVisibility(View.VISIBLE);

                    }


                    mPaginatedVideos = new ArrayList<>();
                    for(int i = 0; i < videos.size(); i++){
                        mPaginatedVideos.add(videos.get(i));
                    }

//                    Toast.makeText(mContext, "Videos" + mPaginatedVideos, Toast.LENGTH_SHORT).show();
                    ProfileVideoListAdapter adapter = new ProfileVideoListAdapter(getActivity(),R.layout.layout_view_video_post,
                            mPaginatedVideos);
                    listView.setAdapter(adapter);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }


        });

    }

    /**
     * Responsible for setting up the profile toolbar
     */

    private void setupToolbar() {

        ((activity_profile) getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings.");
                Intent intent = new Intent(mContext, activity_accountSetting.class);
                startActivity(intent);
            }
        });
    }

//    private void setupViewPager() {
//        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getFragmentManager());
//
//        adapter.addFragment(new FragmentViewGridPhoto());
//        adapter.addFragment(new FragmentViewGridVideo());
//
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.getTabAt(0).setIcon(R.drawable.gallerytab);
//        tabLayout.getTabAt(1).setIcon(R.drawable.videotab);
//        viewPager.setCurrentItem(0);
//
//    }


    //***************** Verifying Permission ***********************//


    private void verifyPermission(String[] permissions) {

        Log.d(TAG, "verifyPermission: Verifying Permissions");
        // Request Code can be Any integer

        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE);
    }


    private boolean checkPermissionArray(String[] permissions) {

        Log.d(TAG, "checkPermissionArray: Checking Permission For all strings in Array");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkpermission(check)) {
                return false;
            }
        }

        return true;
    }


    private boolean checkpermission(String permission) {

        Log.d(TAG, "checkpermission: Checking every single Permission.");
        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkpermission: Permission not Granted.");
            return false;
        } else {
            Log.d(TAG, "checkpermission: Permission Granted Succesfully.");
            return true;
        }

    }



    /*---------------------Firebase Stuff ---------------------------*/

        private void setupFirebaseAuth(){
            firebaseDatabase = FirebaseDatabase.getInstance();
            myRef = firebaseDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = mAuth.getCurrentUser();


                    if (user!= null){
                        Log.d("komu", "onAuthStateSignedIn : User is logged In" + user.getUid());
                    }else {
                        Log.d("komu", "onAuthStateSignedIn : User is logged out");

                    }
                }
            };

        }


        @Override
        public void onStart() {
            super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
            mAuth.addAuthStateListener(mAuthListener);
            FirebaseUser user = mAuth.getCurrentUser();
        }

        @Override
        public void onStop() {
            super.onStop();
            mAuth.removeAuthStateListener(mAuthListener);
        }
}






















