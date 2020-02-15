package in.komu.komu.Home;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import in.komu.komu.MainActivity;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Search.SearchActivity;
import in.komu.komu.share.ShareActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback{

    public static CameraFragment create() {
        return new CameraFragment();
    }


    private Button profile;
    public Camera camera;
    public SurfaceView mSurfaceView;
    public SurfaceHolder mSurfaceHolder;
    private TextView etSearch;
    private Button switchToCamera;
    private Button switchToVideo;
    private Button videoCapture;
    private ImageView notification;

    public Camera.PictureCallback jpegCallback;
    private static final int VIDEO_CAPTURE = 101;

    final int CAMERA_REQUEST_CODE = 1;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera , container, false);

        //Initialise widgets
//        switchToCamera = view.findViewById(R.id.cameraSwitch);
//        switchToVideo = view.findViewById(R.id.videoSwitch);
//        videoCapture = view.findViewById(R.id.videoCapture);
        notification = view.findViewById(R.id.notification);


//        videoCapture.setVisibility(View.GONE);
//        switchToCamera.setVisibility(View.GONE);


//        switchToVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoCapture.setVisibility(View.VISIBLE);
//                switchToCamera.setVisibility(View.VISIBLE);
//                switchToVideo.setVisibility(View.GONE);
//            }
//        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityNotification.class);
                startActivity(intent);
            }
        });


//        videoCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(camera!=null) {
//                    camera.stopPreview();
//                    camera.setPreviewCallback(null);
//
//                    camera.release();
//                    camera = null;
//                }
//                Intent intent = new Intent(getContext(), VideoCapture.class);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        });



        // Hide Keyboard
//        hideSoftKeyboard();


        //profile Button
        profile =  view.findViewById(R.id.profileButton);
        profile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Log.d("onClick: profileButton", "Button profile is Clicked.");
                Intent intent = new Intent(getContext(), activity_profile.class);
                ActivityOptions.makeCustomAnimation(getContext(), R.anim.fade_in, R.anim.fade_out);
                startActivity(intent);
            }
        });

        //Surface View and Surface holder
        mSurfaceView = view.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();

        // we need to create if else for android 6.0 and
        // upper version because they need permission after we install them


        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        // Search Button
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

//        // Rough Button
//        Button switch_camera = view.findViewById(R.id.switch_camera);
//        switch_camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), ShareActivity.class);
//                startActivity(intent);
//            }
//        });

        //Capture Button
//        final Button capture  = view.findViewById(R.id.capture);
//        capture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Run  the captureImage to take Picture. ", Toast.LENGTH_SHORT).show();
//                camera.takePicture(null, null, jpegCallback);
//            }
//        });
//
//        // the things to do after taking picture
//
//        jpegCallback = new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] bytes, Camera camera) {
//
//                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                Bitmap rotateBitmap = rotate(decodedBitmap);
//
//                String fileLocation = SaveImageToStorage(rotateBitmap);
//                if(fileLocation!= null){
//                    Intent intent = new Intent(getActivity(), showCaptureActivity.class);
//                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    startActivity(intent);
//                }
//
//            }
//        };

        // Call the statusBar Color method
        setStatusBarColor(view.findViewById(R.id.statusBarBackground),getResources().getColor(R.color.transparent));

        hideSystemUi();

        return view;
    }

    private void hideSystemUi() {
        mSurfaceView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == VIDEO_CAPTURE) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(getActivity(), "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getActivity(), "Video recording cancelled.", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getActivity(), "Failed to record video", Toast.LENGTH_LONG).show();
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    //    super.onActivityResult(int requestCode, int resultCode, Intent data) {
//
//    }

//    public void myVideoCapture(){
//        videoCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                File mediaFile =
//                        new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                                + "/myvideo.mp4");
//
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//
//                Uri videoUri = Uri.fromFile(mediaFile);
//
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
//                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30);
//                startActivityForResult(intent, VIDEO_CAPTURE);
//            }
//        });
//    }

//    public void myCamera(){
//        camera.takePicture(null, null, jpegCallback);
//
//        // the things to do after taking picture
//
//        jpegCallback = new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] bytes, Camera camera) {
//
//                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                Bitmap rotateBitmap = rotate(decodedBitmap);
//
//                String fileLocation = SaveImageToStorage(rotateBitmap);
//                if(fileLocation!= null){
//                    Intent intent = new Intent(getActivity(), showCaptureActivity.class);
////                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    startActivity(intent);
//                }
//
//            }
//        };
//    }


//    // Hide the keyboard
//    private void hideSoftKeyboard(){
//        if(getActivity().getCurrentFocus() != null){
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//        }
//    }


    // Change the Color of statusBar
    public void setStatusBarColor(View statusBar,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int actionBarHeight = getActionBarHeight();
            int statusBarHeight = getStatusBarHeight();
            //action bar height
            statusBar.getLayoutParams().height = actionBarHeight + statusBarHeight;
            statusBar.setBackgroundColor(color);
        }
    }

    public int getActionBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // Save Image To Storage
    public String SaveImageToStorage(Bitmap bitmap){
        String fileName = "imageToSend";
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        }catch(Exception e){
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap, 0, 0, w, h, matrix, true);

    }



//    private File getFilePath(){
//
//
//
//    }


    //***************** Profile Button ********************//





    // ************* Surface View Stuff ****************//

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//
//        camera = Camera.open();
//
//        Camera.Parameters parameters;
//        parameters = camera.getParameters();
//
//        camera.setDisplayOrientation(90);
//        parameters.setPreviewFrameRate(30);
//
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        // Set the best Size possible to our Camera
//
//        Camera.Size bestSize = null;
//        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
//        bestSize = sizeList.get(0);
//        for(int i = 1; i < sizeList.size(); i++){
//            if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
//                bestSize = sizeList.get(i);
//            }
//        }
//
//        parameters.setPictureSize(bestSize.width, bestSize.height);
//
//        camera.setParameters(parameters);
//
//        try {
//            camera.setPreviewDisplay(surfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        camera.startPreview();


        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //********** catch for request permission **************//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurfaceHolder.addCallback(this);
                    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }else {
                    Toast.makeText(getContext(),"You should give permission to open the app.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {



        Camera.Parameters myParameters = camera.getParameters();
        camera.setDisplayOrientation(90);
        myParameters.setPreviewFrameRate(30);
        myParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size myBestSize = getBestPreviewSize(width, height, myParameters);

        if(myBestSize != null){
            myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
            camera.setParameters(myParameters);
            camera.startPreview();

            Toast.makeText(getContext(),
                    "Best Size:\n" +
                            String.valueOf(myBestSize.width) + " : " + String.valueOf(myBestSize.height),
                    Toast.LENGTH_LONG).show();
        }


    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters){
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        bestSize = sizeList.get(0);

        for(int i = 1; i < sizeList.size(); i++){
            if((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }

        return bestSize;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();

    }

    @Override
    public void onPause() {
        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);

            camera.release();
            camera = null;
        }
        super.onPause();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);

            camera.release();
            camera = null;
        }
    }



}

