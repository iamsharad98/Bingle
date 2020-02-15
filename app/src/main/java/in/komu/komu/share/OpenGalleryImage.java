package in.komu.komu.share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import in.komu.komu.R;
import in.komu.komu.Utils.UniversalImageLoader;

public class OpenGalleryImage extends AppCompatActivity {

    private String imgUrl;
    private int imgUrlInt;

    private static final String TAG = "OpenGalleryImage";
    private String mAppend = "file:/";

    private ImageView imageShared, shareButton;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_galleryimage);
        imageShared = findViewById(R.id.image_shared);
        shareButton = findViewById(R.id.sharepicture);
        mContext = OpenGalleryImage.this;

        // Image Loader
        final String mediaPath = getImage();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
//                Toast.makeText(mContext, "Successfully Clicked " + mAppend +imgURls.get(position), Toast.LENGTH_SHORT).show();
        imageLoader.displayImage(mediaPath, imageShared);
        hideSystemUi();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OpenGalleryImage.this, NextActivity.class);
                i.putExtra("image_shared", mediaPath);
                startActivity(i);
            }
        });

    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        imageShared.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private String getImage(){

        Intent intent;
        intent = getIntent();
        imgUrl = intent.getStringExtra("image_shared");

        return imgUrl;

    }
}




















