package in.komu.komu.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.R;
import in.komu.komu.UserProfile.FragmentViewGridPhoto;
import in.komu.komu.UserProfile.FragmentViewGridVideo;
import in.komu.komu.Utils.ViewCommentFragment;
import in.komu.komu.Utils.ViewPostFragment;
import in.komu.komu.UserProfile.ViewProfileFragment;
import in.komu.komu.Utils.ViewVideoFragment;


public class activity_profile extends AppCompatActivity implements
        ViewPostFragment.OnCommentThreadSelectedListener,
//        FragmentViewGridPhoto.OnGridImageSelectedListener,
        ViewVideoFragment.OnCommentThreadSelectedListener,
        FragmentGridVideo.OnCommentThreadSelectedListener,
        FragmentGridPhoto.OnGridImageSelectedListener
//        ViewProfileFragment.OnViewPagerSelectedListener
        {

    //        FragmentGridVideo.OnGridVideoSelectedListener

    private static final String TAG = "activity_profile";



//    @Override
//    public void OnViewPagerSelected(user_account_setting mUser) {
//        FragmentViewGridPhoto fragment1 = new FragmentViewGridPhoto();
//        FragmentViewGridVideo fragment2 = new FragmentViewGridVideo();
//
//        Bundle args = new Bundle();
//        args.putParcelable("mUser", mUser);
//
//        fragment1.setArguments(args);
//        fragment2.setArguments(args);
//
//    }



    @Override
    public void onCommentThreadSelectedListener(Photo photo) {

        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photos), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }



    @Override
    public void onCommentThreadSelectedListener(Video video) {

        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.videos), video);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void OnGridVideoSelectedListener(Video video, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridView: " + video.toString());

        ViewVideoFragment fragment = new ViewVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.videos), video);
        args.putInt("activity_number", activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_video_fragment));
        transaction.commit();

    }


    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridView: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable("PHOTO", photo);
        args.putInt("activity_number", activityNumber);

        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }


    // Setup widgets
    private ProgressBar progressBar;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = activity_profile.this;


        init();
    }

//    public void hideLayout(){
//        Log.d(TAG, "hideLayout: hiding layout");
//        mRelativeLayout.setVisibility(View.GONE);
//        mFrameLayout.setVisibility(View.VISIBLE);
//    }
//
//
//    public void showLayout(){
//        Log.d(TAG, "hideLayout: showing layout");
//        mRelativeLayout.setVisibility(View.VISIBLE);
//        mFrameLayout.setVisibility(View.GONE);
//    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if(mFrameLayout.getVisibility() == View.VISIBLE){
//            showLayout();
//        }
//    }


    private void init() {

        Log.d("activity_profile", "init started.");

        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if (intent.hasExtra(getString(R.string.intent_user))) {
                user_account_setting user = intent.getParcelableExtra(getString(R.string.intent_user));
                if (!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Log.d(TAG, "init: inflating view profile");
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
                    //View Profile Fragment
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();

//                    //Fragment View Grid Photo
//                    FragmentViewGridPhoto fragment2 = new FragmentViewGridPhoto();
//                    fragment2.setArguments(args);

//                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
//                    transaction2.replace(R.id.container, fragment2);
//                    transaction2.addToBackStack(getString(R.string.view_profile_fragment));
//                    transaction2.commit();

                    // View Grid Video
//                    FragmentViewGridVideo fragment1 = new FragmentViewGridVideo();
//                    fragment1.setArguments(args);

//                    FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
//                    transaction1.replace(R.id.container, fragment1);
//                    transaction1.addToBackStack(getString(R.string.view_profile_fragment));
//                    transaction1.commit();
                } else {
                    Log.d(TAG, "init: inflating Profile");
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = activity_profile.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.profile_fragment));
                    transaction.commit();
                }
            } else {
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else {
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction fragmentTransaction = activity_profile.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }

    }



        }
