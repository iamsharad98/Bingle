package in.komu.komu.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import in.komu.komu.R;
import in.komu.komu.share.NextActivity;

public class OpenPicNewsfeed extends AppCompatActivity {

    private String imgUrl;

    private static final String TAG = "OpenPicNewsfeed";

    private Context mContext;

    //Widgets
    private ImageView redHeart, whiteHeart, shareIcon, optionPost, postImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        postImage = findViewById(R.id.image_shared);
        redHeart = findViewById(R.id.image_heart_red);
        whiteHeart = findViewById(R.id.image_heart);
//        shareIcon = findViewById(R.id.shareImage);
//        optionPost = findViewById(R.id.optionPost);

        mContext = OpenPicNewsfeed.this;

        // Image Loader
        String mediaPath = getImage();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
//                Toast.makeText(mContext, "Successfully Clicked " + mAppend +imgURls.get(position), Toast.LENGTH_SHORT).show();
        imageLoader.displayImage(mediaPath, postImage);
        hideSystemUi();
//
//        shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(OpenPicNewsfeed.this, NextActivity.class);
//                i.putExtra("image_shared", imgUrl);
//                startActivity(i);
//            }
//        });

    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        postImage.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private String getImage(){

        Intent intent;
        intent = getIntent();
        imgUrl = intent.getStringExtra(getString(R.string.post_image));

        return imgUrl;

    }
}




















