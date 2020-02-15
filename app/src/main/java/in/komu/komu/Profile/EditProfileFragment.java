package in.komu.komu.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
//import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

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

//import de.hdodenhof.circleimageview.CircleImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import in.komu.komu.Models.userSettings;
import in.komu.komu.Models.users_profile;
import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.UniversalImageLoader;
import in.komu.komu.share.ShareActivity;

public class EditProfileFragment extends AppCompatActivity {

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    //private String userID;
    private userSettings mUserSettings;


    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    private ProgressBar edit_progressbar;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);

        mContext = EditProfileFragment.this;


        mProfilePhoto =  findViewById(R.id.profile_photo);
        mDisplayName =  findViewById(R.id.et_display_name);
        mUsername =  findViewById(R.id.et_username);
        mWebsite =  findViewById(R.id.et_website);
        mDescription =  findViewById(R.id.et_description);
        mEmail =  findViewById(R.id.et_email);
        mPhoneNumber = findViewById(R.id.et_phoneNumber);
        mChangeProfilePhoto =  findViewById(R.id.changeProfilePhoto);
        edit_progressbar = findViewById(R.id.edit_progressbar);
        mFirebaseMethods = new FirebaseMethods(mContext);

        edit_progressbar.setVisibility(View.GONE);

        setupFirebaseAuth();
        getIncomingIntent();



        // back arrow for navigating back to "ProfileActivity"
        ImageView backArrow =  findViewById(R.id.editProfile_backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                finish();
            }
        });

        // CheckMark Button
        ImageView checkmark =  findViewById(R.id.ic_saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
                edit_progressbar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void saveProfileSettings() {

        final String displayname = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final Long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
        final String email = mEmail.getText().toString();

        //case1: if the user made a change to their username
        if(!mUserSettings.getSetting().getUsername().equals(username)){

            checkIfUserNameExist(username);
        }

        // if a user not change the username
        if (!mUserSettings.getUsers_profile().getDisplay_name().equals(displayname)) {
            //update displayName
            mFirebaseMethods.updateUserAccountSettings(displayname, null, null, 0);
        }
        if (!mUserSettings.getUsers_profile().getWebsite().equals(website)) {
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);
        }
        if (!mUserSettings.getUsers_profile().getAbout().equals(description)) {
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);
        }
        if(mUserSettings.getSetting().getPhone_number() != phoneNumber){
            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);
        }
        // if the user change the username
        if (!mUserSettings.getUsers_profile().getUsername().equals(username)) {
            checkIfUserNameExist(username);
        }

        //if the user change the email



    }

    private void checkIfUserNameExist(final String username) {
        myRef = mFirebaseDatabase.getReference();

        Query query = myRef
                .child(String.valueOf(R.string.db_users_profile))
                .orderByChild("username")
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(mContext, "saved username.", Toast.LENGTH_SHORT).show();

                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        try{
                            Log.d(TAG, "checkIfUsernameExists: FOUND A Match: " + singleSnapshot.getValue(users_profile.class).getUsername());
                            Toast.makeText(mContext, "That username already exists.", Toast.LENGTH_SHORT).show();

                        }catch(NullPointerException e ){
                            Log.d(TAG, "onDataChange: Null Pointer Exception at for Loop");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setProfileWidgets(userSettings user_settings) {
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
        //User user = userSettings.getUser();

        mUserSettings = user_settings;
        users_profile usersProfile = user_settings.getUsers_profile();
//        UniversalImageLoader.setImage(usersProfile.getProfile_photo(), mProfilePhoto, null, "");
//
        Glide
                .with(getApplicationContext())
                .load(usersProfile.getProfile_photo())
                .apply(new RequestOptions()
                .placeholder(R.mipmap.ic_launcher)
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.loading_image))
                .into(mProfilePhoto);

        mUsername.setText(usersProfile.getUsername());
        mDisplayName.setText(usersProfile.getDisplay_name());
        mWebsite.setText(usersProfile.getWebsite());
        mDescription.setText(usersProfile.getAbout());
        mEmail.setText(user_settings.getSetting().getEmail());
        mPhoneNumber.setText(String.valueOf(user_settings.getSetting().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(mContext, ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                startActivity(intent);
                finish();

            }
        });

    }

    private void getIncomingIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra("image_shared")) {

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {

                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(EditProfileFragment.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            intent.getStringExtra("image_shared"), null);


//                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
//                    //set the new profile picture
//                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
//                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
//                            null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
//                }

            }

        }


    }

    /*---------------------Firebase Stuff ---------------------------*/

    private void setupFirebaseAuth() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();
                //Check if the current user is logged in

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

                edit_progressbar.setVisibility(View.GONE);

                // retreiver database from user
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
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
        //FirebaseUser user = mAuth.getCurrentUser();

    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

}




// ****************************************  When We Use Fragment Instead of Activity **********************************************************


    //
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        Log.d(TAG, "EditProfileFragment started.");
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
//
//        //mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
//        mDisplayName = (EditText) view.findViewById(R.id.et_display_name);
//        mUsername = (EditText) view.findViewById(R.id.et_username);
//        mWebsite = (EditText) view.findViewById(R.id.et_website);
//        mDescription = (EditText) view.findViewById(R.id.et_description);
//        mEmail = (EditText) view.findViewById(R.id.et_email);
//        mPhoneNumber = (EditText) view.findViewById(R.id.et_phoneNumber);
//        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
//        mFirebaseMethods = new FirebaseMethods(getActivity());
//
//        setupFirebaseAuth();
//
////        //setProfileImage();
////        setupFirebaseAuth();
//
//        // back arrow for navigating back to "ProfileActivity"
//        ImageView backArrow = (ImageView) view.findViewById(R.id.editProfile_backArrow);
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating back to ProfileActivity");
//                getActivity().finish();
//            }
//        });
//
//        // CheckMark Button
//        ImageView checkmark = (ImageView) view.findViewById(R.id.ic_saveChanges);
//        checkmark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: attempting to save changes.");
//               saveProfileSettings();
//            }
//        });
//
//        return view;
//
//    }
//
//    private void saveProfileSettings(){
//
//        final String displayname = mDisplayName.getText().toString();
//        final String username = mUsername.getText().toString();
//        final String website = mWebsite.getText().toString();
//        final String description = mDescription.getText().toString();
//        final Long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());
//        final String email = mEmail.getText().toString();
//
//        // if a user not change the username
//        if(!mUserSettings.getUsers_profile().getDisplay_name().equals(displayname)){
//            //update displayname
//            mFirebaseMethods.updateUserAccountSettings(displayname, null, null, 0);
//        }
//        if(!mUserSettings.getUsers_profile().getWebsite().equals(website)){
//            //update website
//            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);
//        }
//        if(!mUserSettings.getUsers_profile().getAbout().equals(description)){
//            //update description
//            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);
//        }
////        if(!mUserSettings.getSetting().getPhone_number().equals()){
////            //update phoneNumber
////            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);
////        }
//        // if the user change the username
//        if(!mUserSettings.getUsers_profile().getUsername().equals(username)){
//            checkIfUserNameExist(username);
//        }
//
//        //if the user change the email
//
//    }
//
//    private void checkIfUserNameExist(final String username) {
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
//
//        Query query = myRef
//                .child(String.valueOf(R.string.db_users_profile))
//                .orderByChild("username")
//                .equalTo(username);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(!dataSnapshot.exists()){
//                    //add the username
//                    mFirebaseMethods.updateUsername(username);
//                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();
//
//                }
//                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
//                    if (singleSnapshot.exists()){
//                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(users_profile.class).getUsername());
//                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    private void setProfileWidgets(userSettings user_settings) {
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
//        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());
//        //User user = userSettings.getUser();
//
//        mUserSettings = user_settings;
//        users_profile usersProfile = user_settings.getUsers_profile();
//        //UniversalImageLoader.setImage(usersProfile.getProfile_photo(), mProfilePhoto, null, "");
//
//        mUsername.setText(usersProfile.getUsername());
//        mDisplayName.setText(usersProfile.getDisplay_name());
//        mWebsite.setText(usersProfile.getWebsite());
//        mDescription.setText(usersProfile.getAbout());
//        mEmail.setText(user_settings.getSetting().getEmail());
//        mPhoneNumber.setText(String.valueOf(user_settings.getSetting().getPhone_number()));
//
//    }
//
//    /*---------------------Firebase Stuff ---------------------------*/
//
//    private void setupFirebaseAuth(){
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        myRef = mFirebaseDatabase.getReference();
//        mAuth = FirebaseAuth.getInstance();
////        mAuthListener = new FirebaseAuth.AuthStateListener() {
////            @Override
////            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////                FirebaseUser user = mAuth.getCurrentUser();
////
////
////                if (user!= null){
////                    Log.d("komu", "onAuthStateSignedIn : User is logged In" + user.getUid());
////                }else {
////                    Log.d("komu", "onAuthStateSignedIn : User is logged out");
////
////                }
////            }
////        };
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                // retreiver database from user
//                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        mAuth.addAuthStateListener(mAuthListener);
//        //FirebaseUser user = mAuth.getCurrentUser();
//
//    }
//
//   @Override
//    public void onStop() {
//        super.onStop();
//        mAuth.removeAuthStateListener(mAuthListener);
//    }



















