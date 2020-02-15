package in.komu.komu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
//import android.widget.EditText;
//import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;

import in.komu.komu.Authentication.LoginActivity;
import in.komu.komu.Home.CameraFragment;
import in.komu.komu.Home.NewsFeedFragment;
import in.komu.komu.Home.NewsFeedPhotoFragment;
import in.komu.komu.Home.StoryFragment;
import in.komu.komu.Home.setupPagerAdapter;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Utils.MainTabsView;
import in.komu.komu.Utils.MainfeedListAdapter;
import in.komu.komu.Utils.ViewCommentFragment;
import in.komu.komu.Utils.ViewVideoCommentFragment;
import in.komu.komu.Utils.ViewVideoFragment;
import in.komu.komu.share.ConvertUriToString;
import in.komu.komu.share.NextActivity;
import in.komu.komu.share.OpenGalleryImage;


public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    private MainTabsView mainTabs;
    private ImageView mCameraButton;

    public Camera.PictureCallback jpegCallback;
    private static final int VIDEO_CAPTURE = 101;

    final int CAMERA_REQUEST_CODE = 1;
    private Bitmap imageBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.viewpagerContainer);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        setupFirebaseAuth();
        // ----ViewPager Import-----//
        setupViewPager();
    }

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


    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentFragment fragment  = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photos), photo);
        args.putString(getString(R.string.home_activity), callingActivity);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {

                final Uri thumbPath = data.getData();

                String getPathFromUri;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    getPathFromUri = ConvertUriToString.getPathFromUri(MainActivity.this, thumbPath);
                    Intent i = new Intent(MainActivity.this, OpenGalleryImage.class);
                    i.putExtra("image_shared","file:/" + getPathFromUri);
                    startActivity(i);

                }else{
                    Toast.makeText(MainActivity.this,
                            "You should have atLeast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                }



            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }

        else if(requestCode == CAMERA_REQUEST_CODE){

            if (resultCode == RESULT_OK) {
//
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");

                Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);

//
//                String path = getRealPathFromURI(tempUri);
//                Intent i = new Intent(MainActivity.this, OpenGalleryImage.class);
//                i.putExtra("image_shared", path);
//                startActivity(i);
//
                /// My own way

//                final Uri thumbPath = data.getData();

//                Toast.makeText(mContext, "Image Path " + thumbPath, Toast.LENGTH_SHORT).show();

                String getPathFromUri;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    getPathFromUri = ConvertUriToString.getPathFromUri(MainActivity.this, tempUri);
//                    Toast.makeText(mContext, "path " + getPathFromUri, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, OpenGalleryImage.class);
                    i.putExtra("image_shared", "file:/" + getPathFromUri );
                    startActivity(i);
                }else{
                    Toast.makeText(MainActivity.this, "You should have atLeast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                }



            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "Picture taking process is cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to take Picture", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public String getRealPathFromURI(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        return cursor.getString(idx);
//    }







    // ---------------------ViewPager Stuff---------------------//

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager(){
        mainTabs = findViewById(R.id.mainTabs);
        mCameraButton = mainTabs.getCenterView();

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewPager.getCurrentItem() == 1) {
//                    Toast.makeText(MainActivity.this, "only clicked", Toast.LENGTH_SHORT).show();


                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                }else{
                    mViewPager.setCurrentItem(1, true);
                }
            }
        });

        mCameraButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(mContext, "long clicked ", Toast.LENGTH_SHORT).show();
//                fragment.myVideoCapture();


                File mediaFile =
                        new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/myvideo.mp4");

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                Uri videoUri = Uri.fromFile(mediaFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30);
                startActivityForResult(intent, VIDEO_CAPTURE);

                return true;

            }
        });


        setupPagerAdapter adapter = new setupPagerAdapter(getSupportFragmentManager());
        // add Fragments to ViewPager
        adapter.addFragment(new NewsFeedFragment());
        adapter.addFragment(new CameraFragment());
        adapter.addFragment(new StoryFragment());
        ViewPager viewPager = findViewById(R.id.viewpagerContainer);
        viewPager.setAdapter(adapter);
        mainTabs.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }






    /*---------------------Firebase Stuff ---------------------------*/
//
//        private Context mContext;
//        private FirebaseAuth mAuth;
//        private FirebaseAuth.AuthStateListener mAuthListener;
//
//        private void checkCurrentUser(FirebaseUser user){
//            mContext = MainActivity.this;
//            Log.d("komu","check if user is signed in ");
//            if (user == null){
//                Log.d("komu", "User is not signed in.");
//                Intent intent = new Intent(mContext, LoginActivity.class);
//                startActivity(intent);
//            }
//        }
//
//        private void setupFirebaseAuth(){
//            mAuth = FirebaseAuth.getInstance();
//            mAuthListener = new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    //Check if the current user is logged in
//                    checkCurrentUser(user);
//
//                    if (user!= null){
//                        Log.d("komu", "onAuthStateSignedIn : User is logged In" + user.getUid());
//                    }else {
//                        Log.d("komu", "onAuthStateSignedIn : User is logged out");
//
//                    }
//                }
//            };
//        }
//
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            // Check if user is signed in (non-null) and update UI accordingly.
//            mAuth.addAuthStateListener(mAuthListener);
//            FirebaseUser user = mAuth.getCurrentUser();
//            checkCurrentUser(user);
//
//        }
//
//
//        @Override
//        protected void onStop() {
//            super.onStop();
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
}



