package in.komu.komu.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
//import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import in.komu.komu.MainActivity;
import in.komu.komu.Models.userSettings;
import in.komu.komu.Models.users_profile;
import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.Permissions;
import in.komu.komu.Utils.ProfileVideoListAdapter;
import in.komu.komu.Utils.SectionsPagerAdapter;
//import in.komu.komu.Utils.UniversalImageLoader;

//Universal Image Loader


public class ProfileFragment extends Fragment implements ProfileVideoListAdapter.OnLoadMoreItemsListener {

    private static final String TAG = "ProfileFragment";

    @Override
    public void onLoadMoreItems() {

        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        FragmentGridVideo fragment = (FragmentGridVideo) getChildFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpagerContainer + ":" + viewPager.getCurrentItem());
        if (fragment != null) {
            fragment.displayMoreVideos();
        }
    }

    private int ACTIVITY_NUM = 2;


//
//    public interface OnGridImageSelectedListener{
//        void onGridImageSelected(Photo photo, int activityNumber);
//    }
//    OnGridImageSelectedListener mOnGridImageSelectedListener;
//


    // var
    private static int NUM_GRID_COLUMNS = 3;

    //firebase stuff
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;

    // Widgets
    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, mOrigin, mEditProfile;
    //private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu, backArrow;

    //vars
    private int mFollowersCount = 0;
    private int mFollowingCount = 0;
    private int mPostsCount = 0;
    private ViewPager viewPager;

    private static final int REQUEST_CODE = 1;

    private TabLayout tabLayout;
    private DatabaseReference mRef2, mRef3;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // initiate Widgets
        mDisplayName = view.findViewById(R.id.display_name);
//        mOrigin = view.findViewById(R.id.origin);
        mUsername = view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);
        gridView = view.findViewById(R.id.gridView);
        toolbar = view.findViewById(R.id.profileToolBar);
        profileMenu = view.findViewById(R.id.profileMenu);
        mEditProfile = view.findViewById(R.id.textEditProfile);
        backArrow = view.findViewById(R.id.backArrow);
        viewPager = view.findViewById(R.id.viewpager_container);
        tabLayout = view.findViewById(R.id.topTabs);


        mContext = getActivity();


//        setupGridView();

        // Call ViewPager
        setupViewPager();

        mRef2 = FirebaseDatabase.getInstance().getReference()
        .child(getString(R.string.db_users_profile))
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        ;

        mRef2.keepSynced(true);
        mRef3 = FirebaseDatabase.getInstance().getReference()
        .child(getString(R.string.db_user_account_setting))
        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
        ;
        mRef3.keepSynced(true);


        if (checkPermissionArray(Permissions.PERMISSIONS)) {
            setupViewPager();

        } else {
            verifyPermission(Permissions.PERMISSIONS);
        }

        //Follower and Following
        getFollowersCount();
        getFollowingCount();

        firebaseMethods = new FirebaseMethods(getActivity());

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // EditProfile Button

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to " + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(getActivity(), EditProfileFragment.class);
                intent.putExtra("calling_activity", "Profile Activity");
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);

            }
        });

        // Toolbar Setup
        setupToolbar();


        // Call the back button method
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(TAG, "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.i(TAG, "onKey Back listener is working!!!");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });
        // Firebase stuff
        setupFirebaseAuth();


        return view;
    }

//    // Control the Back Button
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if(keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            Intent intent = new Intent(getActivity(), VideoActivity.class);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            return true;
//        }
//        return false;
//    }


    private void getFollowersCount() {
        mFollowersCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_follower))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found follower:" + singleSnapshot.getValue());
                    mFollowersCount++;
                }
                mFollowers.setText(String.valueOf(mFollowersCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setupViewPager();
//        setupFirebaseAuth();
//    }


    private void getFollowingCount() {
        mFollowingCount = 0;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.db_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found following user:" + singleSnapshot.getValue());
                    mFollowingCount++;
                }
                mFollowing.setText(String.valueOf(mFollowingCount));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void setProfileWidgets(userSettings user_settings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
        //User user = userSettings.getUser();

        users_profile usersProfile = user_settings.getUsers_profile();
//        UniversalImageLoader.setImage(usersProfile.getProfile_photo(), mProfilePhoto, null, "");



        mUsername.setText(usersProfile.getUsername());
        mDisplayName.setText(usersProfile.getDisplay_name());
        mWebsite.setText(usersProfile.getWebsite());
        mDescription.setText(usersProfile.getAbout());

        Glide.with(getContext().getApplicationContext())
                .load(usersProfile.getProfile_photo())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_prof))
                .into(mProfilePhoto);

//        mOrigin.setText(usersProfile.getOrigin());
    }
//
//    private void setupGridView() {
//        Log.d(TAG, "setupGridView: Setting up image grid.");
//
//        final ArrayList<Photo> photos = new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.db_user_photos))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//
//                    Photo photo = new Photo();
//                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
//                    try {
//                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
//                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
//                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
//                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
//                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
//                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
//
//                        List<Comment> mComments = new ArrayList<Comment>();
//
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child(mContext.getString(R.string.field_comments)).getChildren()){
//                            Comment comment = new Comment();
//                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                            mComments.add(comment);
//                        }
//
//                        photo.setComments(mComments);
//
//                        List<Like> likesList = new ArrayList<Like>();
//                        for (DataSnapshot dSnapshot : singleSnapshot
//                                .child(getString(R.string.field_likes)).getChildren()) {
//                            Like like = new Like();
//                            like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                            likesList.add(like);
//                        }
//
//                        photo.setLikes(likesList);
//                        photos.add(photo);
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
//                            mOnGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
//
//                        }
//                    });
//
//                    ArrayList<String> imgUrls = new ArrayList<String>();
//                    for(int i = 0; i < photos.size(); i++){
//                        imgUrls.add(photos.get(i).getImage_path());
//                    }
//                    GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,
//                            "", imgUrls);
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

    // ************* Setup ViewPager **********************//

    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getFragmentManager());

        adapter.addFragment(new FragmentGridPhoto());//0
        adapter.addFragment(new FragmentGridVideo());//1

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.gallerytab);
        tabLayout.getTabAt(1).setIcon(R.drawable.videotab);
        viewPager.setCurrentItem(0);

    }


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

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // retreiver database from user
                    setProfileWidgets(firebaseMethods.getUserSettings(dataSnapshot));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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






















