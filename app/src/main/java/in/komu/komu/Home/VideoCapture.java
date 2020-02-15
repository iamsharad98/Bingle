package in.komu.komu.Home;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.komu.komu.R;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class VideoCapture extends Activity implements  SurfaceHolder.Callback {

    private static final String TAG = "VideoCapture";

    private final String VIDEO_PATH_NAME = "/mnt/sdcard/VGA_30fps_512vbrate.mp4";

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private View mToggleButton;
    private boolean mInitSuccesful;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        // we shall take the video in landscape orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mToggleButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // toggle video recording
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked()) {
                    mMediaRecorder.start();
                    try {
                        Thread.sleep(10 * 1000); // This will recode for 10 seconds, if you don't want then just remove it.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    mMediaRecorder.stop();
                    mMediaRecorder.reset();
                    try {
                        initRecorder(mHolder.getSurface());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /* Init the MediaRecorder, the order the methods are called is vital to
     * its correct functioning */
    private void initRecorder(Surface surface) throws IOException {
        // It is very important to unlock the camera before doing setCamera
        // or it will results in a black preview
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.unlock();
        }

        if (mMediaRecorder == null) mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setPreviewDisplay(surface);
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //       mMediaRecorder.setOutputFormat(8);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace();
        }

        mInitSuccesful = true;
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (!mInitSuccesful)
                initRecorder(mHolder.getSurface());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        shutdown();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    private void shutdown() {
        // Release MediaRecorder and especially the Camera as it's a shared
        // object that can be used by other applications
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mCamera.release();

        // once the objects have been released they can't be reused
        mMediaRecorder = null;
        mCamera = null;
    }
}


//    public static final String LOGTAG = "VIDEOCAPTURE";
//
//    private MediaRecorder recorder;
//    private SurfaceHolder holder;
//    private CamcorderProfile camcorderProfile;
//    private Camera camera;
//
//    boolean recording = false;
//    boolean usecamera = true;
//    boolean previewRunning = false;
//    private SurfaceView cameraView;
//
//    final int CAMERA_REQUEST_CODE = 1;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_capture);
//
//        Toast.makeText(this, "VideoCapture is started", Toast.LENGTH_SHORT).show();
////        camera = null;
//        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
//
//        cameraView = (SurfaceView) findViewById(R.id.CameraView);
//        holder = cameraView.getHolder();
////        holder.addCallback(this);
////        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//        }else{
//            holder.addCallback(this);
//            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        }
//
//        if (camera != null) {
//            camera.lock();
//            camera.stopPreview();
//            camera.release();
//            camera = null;
//        }
//
//        cameraView.setClickable(true);
//        cameraView.setOnClickListener(this);
//    }
//
//    private void prepareRecorder() {
//        recorder = new MediaRecorder();
//        recorder.setPreviewDisplay(holder.getSurface());
//
//        if (usecamera) {
//            camera.unlock();
//            recorder.setCamera(camera);
//        }
//
//        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//
//        recorder.setProfile(camcorderProfile);
//
//        // This is all very sloppy
//        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.THREE_GPP) {
//            try {
//                File newFile = File.createTempFile("videocapture", ".3gp", Environment.getExternalStorageDirectory());
//                recorder.setOutputFile(newFile.getAbsolutePath());
//            } catch (IOException e) {
//                Log.v(LOGTAG,"Couldn't create file");
//                e.printStackTrace();
//                finish();
//            }
//        } else if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
//            try {
//                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
//                recorder.setOutputFile(newFile.getAbsolutePath());
//            } catch (IOException e) {
//                Log.v(LOGTAG,"Couldn't create file");
//                e.printStackTrace();
//                finish();
//            }
//        } else {
//            try {
//                File newFile = File.createTempFile("videocapture", ".mp4", Environment.getExternalStorageDirectory());
//                recorder.setOutputFile(newFile.getAbsolutePath());
//            } catch (IOException e) {
//                Log.v(LOGTAG,"Couldn't create file");
//                e.printStackTrace();
//                finish();
//            }
//
//        }
//
//        recorder.setMaxDuration(50000); // 50 seconds
//        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
//
//        try {
//            recorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//            finish();
//        } catch (IOException e) {
//            e.printStackTrace();
//            finish();
//        }
//    }
//
//    public void onClick(View v) {
//        if (recording) {
//            recorder.stop();
//            if (usecamera) {
//                try {
//                    camera.reconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            // recorder.release();
//            recording = false;
//            Log.v(LOGTAG, "Recording Stopped");
//            // Let's prepareRecorder so we can record again
//            prepareRecorder();
//        } else {
//            recording = true;
//            recorder.start();
//            Log.v(LOGTAG, "Recording Started");
//        }
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        Log.v(LOGTAG, "surfaceCreated");
//
//        if (usecamera) {
//
//            try {
//                releaseCameraAndPreview();
//                camera = Camera.open();
//            } catch (Exception e) {
//                Log.e(getString(R.string.app_name), "failed to open Camera");
//                e.printStackTrace();
//            }
//
//            try {
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//                previewRunning = true;
//            }
//            catch (IOException e) {
//                Log.e(LOGTAG,e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA_REQUEST_CODE:{
//                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    holder.addCallback(this);
//                    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//                }else {
//                    Toast.makeText(this,"You should give permission to open the app.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
//
//    private void releaseCameraAndPreview() {
//        recorder.setCamera(null);
//       if (camera != null) {
//            camera.release();
//            camera = null;
//        }
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.v(LOGTAG, "surfaceChanged");
//
//        if (!recording && usecamera) {
//            if (previewRunning){
//                camera.stopPreview();
//            }
//
//            try {
//                Camera.Parameters p = camera.getParameters();
//
//                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
//                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);
//
//                camera.setParameters(p);
//
//                camera.setPreviewDisplay(holder);
//                camera.startPreview();
//                previewRunning = true;
//            }
//            catch (IOException e) {
//                Log.e(LOGTAG,e.getMessage());
//                e.printStackTrace();
//            }
//
//            prepareRecorder();
//        }
//    }
//
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.v(LOGTAG, "surfaceDestroyed");
//        if (recording) {
//            recorder.stop();
//            recording = false;
//        }
//        recorder.release();
//        if (usecamera) {
//            previewRunning = false;
//            //camera.lock();
//            camera.release();
//        }
//        finish();
//    }
