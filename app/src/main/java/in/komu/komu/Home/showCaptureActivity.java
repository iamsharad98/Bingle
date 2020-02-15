package in.komu.komu.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import in.komu.komu.MainActivity;
import in.komu.komu.R;

public class showCaptureActivity extends AppCompatActivity {

    private static final String TAG = "showCaptureActivity";
    private Context mContext;
    private Bitmap bitmap;

    // Vars
    private double mPhotoUploadProgress = 0;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        mContext = showCaptureActivity.this;
        ImageView imageCapture = findViewById(R.id.imageCapture);


        //Log.d("showCaptureImage", "start the new Activity  to collect data.");

        Toast.makeText(mContext, "start the new Activity  to collect data ", Toast.LENGTH_SHORT).show();

        // UID
        Uid = FirebaseAuth.getInstance().getUid();

        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput("imageToSend"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            finish();
            return;
        }

        imageCapture.setImageBitmap(bitmap);

        Button savePicture = findViewById(R.id.savePicture);
        savePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

    }

    private void saveImage() {
        Toast.makeText(mContext, "Image Saving is in Progress...", Toast.LENGTH_SHORT).show();
        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("userSettings").child(Uid).child("post");
        final String key = userStoryDb.push().getKey();

        assert key != null;
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] dataToUpload = baos.toByteArray();
        UploadTask uploadTask = filePath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUrl) {

                        Map<String, Object> mapToUpload = new HashMap<>();

                        mapToUpload.put("imageUrl", imageUrl.toString());
                        userStoryDb.child(key).setValue(mapToUpload);


                        Intent intent = new Intent(showCaptureActivity.this, MainActivity.class);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        finish();

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Image Saved Failed.", Toast.LENGTH_LONG).show();

                finish();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (progress- 10 > mPhotoUploadProgress){
                    Toast.makeText(mContext, "Photo Upload Progress "+ String.format("%.0f", progress)+ "%", Toast.LENGTH_SHORT).show();
                    mPhotoUploadProgress = progress;
                }
                Log.d(TAG, "onProgress: Photo Upload Progress" + progress);
            }
        });
  }


}
