package in.komu.komu.Utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Models.userSettings;
import in.komu.komu.Models.user_account_setting;
import in.komu.komu.Models.users_profile;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;


public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress;
    private Bitmap bitmap;
//    private String newPhotoKey;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;


        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    // Update User Account Setting If someone change the Setting

    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber){

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if(displayName != null){
            myRef.child(mContext.getString(R.string.db_user_account_setting))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
            myRef.child(mContext.getString(R.string.db_users_profile))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if(website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
            myRef.child(mContext.getString(R.string.db_users_profile))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
            myRef.child(mContext.getString(R.string.db_users_profile))
                    .child(userID)
                    .child("about")
                    .setValue(description);
        }

        if(phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

    //  Update Username

    /**
     * update username in the 'users' node and 'user_account_settings' node
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.db_users_profile))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    public boolean checkIfUserNameExist(String username, DataSnapshot dataSnapshot){
        users_profile user = new users_profile();

        for(DataSnapshot ds: dataSnapshot.child(userID).getChildren()){
            user.setUsername(ds.getValue(users_profile.class).getUsername());
            if (StringManipulation.expandUsername(user.getUsername()).equals(username)){
                return true;
            }

        }
        return false;
    }
    // Sending Verification Email -------------//

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */

    public void registerNewEmail(final String email, String password, final String username){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            //send verificaton email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Toast.makeText(mContext, R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(mContext, "Verification Email is sent to your Email. ", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }

    public void addNewUser(String email, String username, String about, String profile_photo, String origin){

        users_profile user = new users_profile(username, StringManipulation.condenseUsername(username), "", userID, profile_photo,origin, about, 0, 0,0 );

        myRef.child(mContext.getString(R.string.db_users_profile))
                .child(userID)
                .setValue(user);

        user_account_setting userAccountSetting = new user_account_setting(userID,StringManipulation.condenseUsername(username),0,email);
        myRef.child(mContext.getString(R.string.db_user_account_setting))
                .child(userID)
                .setValue(userAccountSetting);
    }

    public userSettings getUserSettings(DataSnapshot dataSnapshot){

        Log.d("FirebaseMethods", "getUserSetting started.");

        user_account_setting setting =  new user_account_setting();
        users_profile usersProfile = new users_profile();

        for(DataSnapshot ds: dataSnapshot.getChildren()) {

            if(ds.getKey().equals(mContext.getString(R.string.db_users_profile))) {
                Log.d(TAG, "getUserSettings: user account settings node datasnapshot: " + ds);

                try {

                    usersProfile.setUsername(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getUsername()
                    );
                    usersProfile.setAbout(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getAbout()
                    );
                    usersProfile.setDisplay_name(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getDisplay_name()
                    );
                    usersProfile.setFollowers(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getFollowers()
                    );

                    usersProfile.setFollowing(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getFollowing()
                    );

                    usersProfile.setPost(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getPost()
                    );
                    usersProfile.setOrigin(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getOrigin()
                    );
                    usersProfile.setProfile_photo(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getProfile_photo()
                    );
                    usersProfile.setUser_id(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getUser_id()
                    );
                    usersProfile.setWebsite(
                            ds.child(userID)
                                    .getValue(users_profile.class)
                                    .getWebsite()
                    );


                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
           }

                        // users node
            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());

            if(ds.getKey().equals(mContext.getString(R.string.db_user_account_setting))) {
                try{

                    setting.setUser_id(
                            ds.child(userID)
                                    .getValue(user_account_setting.class)
                                    .getUser_id()
                    );
                    setting.setPhone_number(
                            ds.child(userID)
                                    .getValue(user_account_setting.class)
                                    .getPhone_number()
                    );
                    setting.setEmail(
                            ds.child(userID)
                                    .getValue(user_account_setting.class)
                                    .getEmail()
                    );
                    setting.setUsername(
                            ds.child(userID)
                                    .getValue(user_account_setting.class)
                                    .getUsername()
                    );
                }catch (NullPointerException e){
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());

                }

            }

        }

        return new userSettings(setting, usersProfile);
    }

    //********************* Image to Share ***********************//

    /**
     * Register a new email and password to Firebase Authentication
     * @param dataSnapshot
     */
    public int getImageCount(DataSnapshot dataSnapshot){

//        Toast.makeText(mContext, "GetImageCount Started", Toast.LENGTH_SHORT).show();
        int imageCount = 0;
        for (DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.user_photos))
                .child(userID)
                .getChildren()
                ){
            imageCount++;
        }
        return imageCount;
    }

    // Count The No. of videos in Database
    public int getVideoCount(DataSnapshot dataSnapshot){

//        Toast.makeText(mContext, "GetImageCount Started", Toast.LENGTH_SHORT).show();
        int videoCount = 0;
        for (DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.db_videos))
                .child(userID)
                .getChildren()
                ){
            videoCount++;
        }
        return videoCount;
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param imagetype
     * @param caption
     * @param imageCount
     * @param imgURL
     */

    public void uploadNewPhoto(String imagetype, final String caption, int imageCount, final String imgURL, Bitmap bm){

        /*** case 1)
         * Upload new Photo
         * case 2)
         * upload new profile photo
         *
         */
        if (imagetype.equals(mContext.getString(R.string.new_photo))){

            FilePaths filePaths = new FilePaths();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/"+ user_id + "/photo" + (imageCount + 1) );

            // convert imageUrl to bitmap
            if (bitmap== null){
                bitmap = ImageManager.getBitmapFromImageURL(imgURL);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bitmap, 50);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);




//            final String newPhotoKey = myRef.child(mContext.getString(R.string.db_photos)).push().getKey();
//            final DatabaseReference db_photos = myRef.child("photos");
//            final DatabaseReference db_user_photos = myRef.child("user_photos").child(userID);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext, "Upload Success :)", Toast.LENGTH_SHORT).show();


                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri imageUrl) {

                          addPhotoToDatabase(caption, imageUrl.toString());


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to getDownload Url");
                            Toast.makeText(mContext, "Failed to get Download Url.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // navigating to feed after upload success
                    Intent intent = new Intent(mContext, activity_profile.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(mContext, " Photo Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress- 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "Photo Upload Progress "+ String.format("%.0f", progress)+ "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: Photo Upload Progress" + progress);
                }
            });


        }else if (imagetype.equals(mContext.getString(R.string.profile_photo))){

            Log.d(TAG, "uploadNewPhoto: Upload new Profile photo");


            FilePaths filePaths = new FilePaths();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/"+ user_id + "/profile_photo");

            // convert imageUrl to bitmap
            if (bitmap== null){
                bitmap = ImageManager.getBitmapFromImageURL(imgURL);
            }

            byte[] bytes = ImageManager.getBytesFromBitmap(bitmap, 50);
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Task<Uri> firebaseUrl = mStorageReference.getDownloadUrl();
                    Toast.makeText(mContext, "Upload Success :)", Toast.LENGTH_SHORT).show();

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri imageUrl) {

                            setProfilePhoto(imageUrl.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Failed to getDownload Url");
                            Toast.makeText(mContext, "Failed to get Download Url.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // add profile photo to Database

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(mContext, " Photo Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress- 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "Photo Upload Progress "+ String.format("%.0f", progress)+ "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: Photo Upload Progress" + progress);
                }
            });

        }
    }

    private void setProfilePhoto(String imgUrl){
        Log.d(TAG, "setProfilePhoto: Uploading new Profile Photo");

//        users_profile usersProfile = new users_profile();
//        usersProfile.setProfile_photo(imgUrl);
        myRef.child("users_profile")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(imgUrl);
    }

    /**
     * Add a new Video To Firebase Storage
     * @param videoType
     * @param caption
     * @param videoCount
     * @param videoUrl
     */

    public void addNewVideoToStorage(String videoType, final String caption, final int videoCount, final String videoUrl,
                                     final String thumbPath, final String category) {

        Log.d(TAG, "addNewVideoToStorage: Add new Video TO firebase Storage");


        Log.d(TAG, "uploadNewStory: uploading new story (VIDEO) to firebase storage.");
        FilePaths filePaths = new FilePaths();


        //Thumbnail Processing

        Toast.makeText(mContext, "ThumbPath" + thumbPath, Toast.LENGTH_SHORT).show();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String contestCategory = category.replace("#", "");
        String videoFolder = "/" + contestCategory +  "/video" + ( videoCount + 1) + "/video" +  ( videoCount + 1)  ;
//        String videoFolder = "/" + contestCategory  ;

//        final StorageReference storageReference1 = mStorageReference
//                .child((filePaths.FIREBASE_VIDEO_STORAGE + "/" + user_id  + videoFolder + "/thumb" ));
//
//        // convert imageUrl to bitmap
//        if (bitmap== null){
//            bitmap = ImageManager.getBitmapFromImageURL(thumbPath);
//        }
//
//        byte[] bytes1 = ImageManager.getBytesFromBitmap(bitmap, 50);
//        UploadTask uploadTask1 = null;
//        uploadTask1 = storageReference1.putBytes(bytes1);
//
//        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
////                    Task<Uri> firebaseUrl = mStorageReference.getDownloadUrl();
////                Toast.makeText(mContext, "Upload Success :)", Toast.LENGTH_SHORT).show();
//
//                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri imageUrl) {
//
////                        setProfilePhoto(imageUrl.toString());
//                        addThumbToDatabase(imageUrl.toString(), category);
//
////                        addNewVideoToDatabase("",null,"", description, imageUrl.toString());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: Failed to getDownload Url");
//                        Toast.makeText(mContext, "Failed to get Download Url.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                // add profile photo to Database
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Toast.makeText(mContext, " Photo Upload Failed", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @SuppressLint("DefaultLocale")
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                if (progress- 15 > mPhotoUploadProgress){
//                    Toast.makeText(mContext, "Photo Upload Progress "+ String.format("%.0f", progress)+ "%", Toast.LENGTH_SHORT).show();
//                    mPhotoUploadProgress = progress;
//                }
//                Log.d(TAG, "onProgress: Photo Upload Progress" + progress);
//            }
//        });
//

        // Video Processing
        final StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_VIDEO_STORAGE + user_id  + videoFolder);
        FileInputStream fis = null;
        String str = videoUrl.replace("file:/", "");
        File file = new File(str);

//        File destinationPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/cVideo.mp4");
//        destinationPath.mkdir();
//        File newFile = new File(destinationPath.getAbsolutePath());
//        Toast.makeText(Post.this, "folder: " + file, Toast.LENGTH_SHORT).show();
//
//        String filePath = null;
//        try {
//            filePath = SiliCompressor.with(Post.this).compressVideo(videouri, file.toString());
//            video.setVideoURI(Uri.parse(filePath));
//            Toast.makeText(Post.this, "Completed", Toast.LENGTH_SHORT).show();
//        } catch (URISyntaxException e) {
//            Log.d("EXCEPTION", e.toString());
//            Toast.makeText(Post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }


//
////        String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/video kit/in.mp4","-strict","experimental","-s", "160x120","-r","25",
////                "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/video kit/out.mp4"};
////
//        String fileToBeCompressed = "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/cVideo.mp4";
//////        ffmpeg -i input.mp4 -acodec mp2 output.mp4
//        ffmpeg -i <inputfilename> -s 640x480 -b:v 512k -vcodec mpeg1video -acodec copy <outputfilename>
//        Controller.getInstance().run(new String[] {
//                "-y",
//                    "-i",
//                    file.getAbsolutePath(),
//                    "-s",
//                    "640*480",
//                    "-b:v",
//                "512k",
//                "-ccodec",
//                "mpeglvideo",
//                "-acodec",
//                "copy",
//                    "file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/cVideo.mp4"
//        });
////
//        File cFile = new File("file:/" + Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/cVideo.mp4");
//        Toast.makeText(mContext, "path of file :-------- " + fileToBeCompressed, Toast.LENGTH_SHORT).show();

        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[0];
        try {
            bytes = readBytes(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "uploadNewStory: video upload bytes: " + bytes.length);
        final byte[] uploadBytes = bytes;
//        String filename = saveVideoToStorage(uploadBytes);
//
//        // Compress Video
//        try {
//            String filePath = SiliCompressor.with(mContext).compressVideo(str , filename);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

//        String filePath = SiliCompressor.with(mContext).compressVideo(str , filename);


        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Task<Uri> firebaseUrl = mStorageReference.getDownloadUrl();
                Toast.makeText(mContext, "Upload Success :)", Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri videoUrl) {

                        uploadThumbnail(videoUrl.toString(), uploadBytes, caption, category, thumbPath, videoCount);

//                        if (deleteCompressedVideo) {
//                            deleteOutputFile(uri);
//                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to getDownload Url");
                        Toast.makeText(mContext, "Failed to get Download Url.", Toast.LENGTH_SHORT).show();
                    }
                });

                // navigating to feed after upload success
//                Intent intent = new Intent(mContext, activity_profile.class);
//                mContext.startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, "Upload Failed", Toast.LENGTH_SHORT).show();
//                if (deleteCompressedVideo) {
//                    deleteOutputFile(uri);
//                }

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (progress - 15 > mPhotoUploadProgress) {
                    Toast.makeText(mContext, "Video Upload Progress " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG, "onProgress: Video Upload Progress" + progress);
            }
        });

    }
//
//    public String saveVideoToStorage(byte[] bytess){
//        String fileName = "imageToSend";
//        try{
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream(bytess.length);
//            bytes.write(bytess, 0, bytess.length);
//
//            FileOutputStream fo = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        }catch(Exception e){
//            e.printStackTrace();
//            fileName = null;
//        }
//        return fileName;
//    }

//    private void addThumbToDatabase(String thumbUrl, String category){
//
//        Video video = new Video();
//        video.setThumb_url(thumbUrl);
//        String newKey = myRef.push().getKey();
//
//        myRef.child(mContext.getString(R.string.db_videos))
//                .child(userID)
//                .child(newKey)
//                .setValue(video);
//        myRef.child(mContext.getString(R.string.db_all_videos))
//                .child(newKey)
//                .setValue(video);
////        myRef.child(mContext.getString(R.string.db_contest))
////                .child(description.getCategory())
////                .child(mContext.getString(R.string.db_videos))
////                .child(newKey)
////                .setValue(video);
//
//    }

    private void deleteOutputFile(@Nullable String uri) {
        if (uri != null)
            //noinspection ResultOfMethodCallIgnored
            new File(Uri.parse(uri).getPath()).delete();
    }


    // Read Bytes Method

    public byte[] readBytes(FileInputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    // Add Video To Database

//    private void addNewVideoToDatabase(String url, byte[] bytes, String caption, String category, String thumbPath, int videoCount){
//        Log.d(TAG, "addNewStoryToDatabase: adding new story to database.");
//
//
//    }

    private boolean isMediaVideo(String uri){
        if(uri.contains(".mp4") || uri.contains(".wmv") || uri.contains(".flv") || uri.contains(".avi")){
            return true;
        }
        return false;
    }

    //ThumbUpload
    private void uploadThumbnail(final String url, final byte[] bytes, final String caption, final String category, final String thumbPath, final int videoCount){
        //Thumbnail Processing

        FilePaths filePaths = new FilePaths();
        Toast.makeText(mContext, "ThumbPath" + thumbPath, Toast.LENGTH_SHORT).show();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String contestCategory = category.replace("#", "");
        String videoFolder = "/" + contestCategory + "/video" +  ( videoCount + 1) ;
        final StorageReference storageReference1 = mStorageReference
                .child((filePaths.FIREBASE_VIDEO_STORAGE + "/" + user_id  + videoFolder + "/thumb" ));

        // convert imageUrl to bitmap
        if (bitmap== null){
            bitmap = ImageManager.getBitmapFromImageURL(thumbPath);
        }

        byte[] bytes1 = ImageManager.getBytesFromBitmap(bitmap, 50);
        UploadTask uploadTask1 = null;
        uploadTask1 = storageReference1.putBytes(bytes1);

        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                    Task<Uri> firebaseUrl = mStorageReference.getDownloadUrl();
//                Toast.makeText(mContext, "Upload Success :)", Toast.LENGTH_SHORT).show();

                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUrl) {

                    String tags = StringManipulation.getTags(caption);

                    Video video = new Video();
                    video.setVideo_url(url);
                    final String newKey = myRef.push().getKey();
                    video.setVideo_id(newKey);
                    video.setTimestamp(getTimestamp());
                    video.setUser_id(userID);
                    video.setViews("0");
                    video.setCaption(caption);
                    video.setTags(tags);
                    video.setThumb_url(imageUrl.toString());
                    video.setCategory(category);

                    // calculate the estimated duration.
                    // need to do this for the progress bars in the block. We can't get the video duration of MP4 files
                    double megabytes = bytes.length / 1000000.000;
                    Log.d(TAG, "addNewVideoToDatabase: estimated MB: " + megabytes);
                    String duration = String.valueOf(Math.round(15 * (megabytes / 6.3)));
                    Log.d(TAG, "addNewVideoToDatabase: estimated video duration: " + duration);
//                        IContainer container = IContainer.make();
//                        int result = container.open(filename, IContainer.Type.READ, null);
//                        long duration = container.getDuration();
//                        long fileSize = container.getFileSize();

                    video.setDuration(duration);

                    //set the Video
                    myRef.child(mContext.getString(R.string.db_videos))
                            .child(userID)
                            .child(newKey)
                            .setValue(video);
                    myRef.child(mContext.getString(R.string.db_all_videos))
                            .child(newKey)
                            .setValue(video);
                    myRef.child(mContext.getString(R.string.db_contest))
                            .child(category.replace("#", ""))
                            .child(mContext.getString(R.string.db_videos))
                            .child(newKey)
                            .setValue(video);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to getDownload Url");
                        Toast.makeText(mContext, "Failed to get Download Url.", Toast.LENGTH_SHORT).show();
                    }
                });
                // add profile photo to Database

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(mContext, " Photo Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (progress- 15 > mPhotoUploadProgress){
                    Toast.makeText(mContext, "Photo Upload Progress "+ String.format("%.0f", progress)+ "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG, "onProgress: Photo Upload Progress" + progress);
            }
        });



    }

    //    private void addPhotoToDatabase(String caption, String url)
    public void addPhotoToDatabase(String caption, String url) {

        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        String tags = StringManipulation.getTags(caption);

        String newPhotoKey = myRef.child(mContext.getString(R.string.db_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
//        Toast.makeText(mContext, "Caption is "+ caption, Toast.LENGTH_SHORT).show();
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.db_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.db_photos)).child(newPhotoKey).setValue(photo);

    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }


}


//            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//
//                    // Continue with the task to get the download URL
//                    return mStorageReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()) {
//                        Uri downloadUri = task.getResult();
//                    } else {
//                        // Handle failures
//                        // ...
//                    }
//                }
//            });








//            settings = ds.child(userId).getValue(UserAccountSettings.class);
//            user = ds.child(userId).getValue(User.class);
//            try{
//
//                if(ds.getKey().equals(mContext.getString(R.string.db_users_profile))) {
//                    usersProfile = ds.child(userID).getValue(users_profile.class);
//
//                }
//                }catch(NullPointerException e ){
//                Log.d("FirebaseMethods","NUll Pointer Exception In db_Users_profile "  );
//            }
//
//            try{
//                if(ds.getKey().equals(mContext.getString(R.string.db_user_account_setting))) {
//                    setting = ds.child(userID).getValue(user_account_setting.class);
//
//                }
//
//            }catch(NullPointerException e ) {
//
//                Log.d("FirebaseMethods", "NUll Pointer Exception In db_user_Setting");
//
//            }




//                try{
//
//                    usersProfile.setUsername(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getUsername()
//                    );
//                    usersProfile.setAbout(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getAbout()
//                    );
//                    usersProfile.setDisplay_name(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getDisplay_name()
//                    );
//                    usersProfile.setFollowers(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getFollowers()
//                    );
//
//                    usersProfile.setFollowing(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getFollowing()
//                    );
//
//                    usersProfile.setPost(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getPost()
//                    );
//                    usersProfile.setOrigin(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getOrigin()
//                    );
//                    usersProfile.setProfile_photo(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getProfile_photo()
//                    );
//                    usersProfile.setUser_id(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getUser_id()
//                    );
//                    usersProfile.setWebsite(
//                            ds.child(userID)
//                                    .getValue(users_profile.class)
//                                    .getWebsite()
//                    );
//
//
//                }catch(NullPointerException e ){
//                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
//                }
//
//            }
//            // users node
//            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
//
//            if(ds.getKey().equals(mContext.getString(R.string.db_user_account_setting))) {
//                try{
//
//                    setting.setUser_id(
//                            ds.child(userID)
//                                    .getValue(user_account_setting.class)
//                                    .getUser_id()
//                    );
//                    setting.setPhone_number(
//                            ds.child(userID)
//                                    .getValue(user_account_setting.class)
//                                    .getPhone_number()
//                    );
//                    setting.setEmail(
//                            ds.child(userID)
//                                    .getValue(user_account_setting.class)
//                                    .getEmail()
//                    );
//                    setting.setUsername(
//                            ds.child(userID)
//                                    .getValue(user_account_setting.class)
//                                    .getUsername()
//                    );
//                }catch (NullPointerException e){
//                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
//
//                }
//
//            }











