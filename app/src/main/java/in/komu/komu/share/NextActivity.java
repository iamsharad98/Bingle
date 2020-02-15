package in.komu.komu.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.komu.komu.Authentication.LoginActivity;
import in.komu.komu.Models.Comment;
//import in.komu.komu.Models.Contest;
import in.komu.komu.Models.Contest;
import in.komu.komu.Models.ContestDescription;
import in.komu.komu.Models.Photo;
import in.komu.komu.Models.Video;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Utils.ContestItemListAdapter;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.GridImageAdapter;
import in.komu.komu.Utils.Like;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";

    private ImageView image;
    private String imgUrl;
    private DatabaseReference myRef;

    private int videoCount;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseMethods mFirebaseMethods;

    //vars
    private String mAppend = "file:/";
    private int imageCount;
    private Bitmap bitmap;
    private Intent intent;

    //Widgets

    private EditText mcaption;
    private String caption;
    private Button chooseThumb;
    private ImageView backArrow;
    private ImageView thumbnail;
    private RelativeLayout relLayout3;
    private TextView shareButton;
    private ListView contestList;
    private RelativeLayout contestSection;
    private RelativeLayout delhiContestItem;
    private RelativeLayout miContestItem;
    private RelativeLayout oppoContestItem;
    private RelativeLayout vivoContestItem;



    public static final int PICK_IMAGE = 1;

    private LinearLayout contestItem;
    // Adapter Vars


//
//    public static final String[] category = new String[] { "#delhiContestTop100",
//            "#bingleMiA1Contest", "#bingleOppoContest", "#bingleVivoContest" };
//
//    public static final String[] reward = new String[] {
//            "Win cash prize of Rs 50,000/-",
//            "Win an Mi A1 phone.",
//            "Win an Oppo phone.",
//            "Win an Vivo phone." };
//    public static final String[] start_date = new String[] {
//            "15 Aug, 2018",
//            "15 Aug, 2018",
//            "15 Aug, 2018",
//            "15 Aug, 2018" };
//    public static final String[] end_date = new String[] {
//            "",
//            "",
//            "",
//            "" };
//
//    public static final Integer[] images = {
//            R.drawable.indianflag2,
//            R.drawable.xiaomimi,
//            R.drawable.oppof7,
//            R.drawable.vivophone };
//
//    public ArrayList<ContestDescription> rowItems;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        chooseThumb = findViewById(R.id.chooseThumb);
        thumbnail  = findViewById(R.id.thumbShare);
        shareButton = findViewById(R.id.tvShare);

        relLayout3 = findViewById(R.id.relLayout3);
        contestItem = findViewById(R.id.contestItem);
        contestSection = findViewById(R.id.contestSection);
//        contestList = findViewById(R.id.contestList);
        delhiContestItem = findViewById(R.id.delhiContestItem);
        miContestItem = findViewById(R.id.miContestItem);
        oppoContestItem = findViewById(R.id.oppoContestItem);
        vivoContestItem = findViewById(R.id.vivoContestItem);


        // Back Button
        backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        relLayout3.setVisibility(View.GONE);
        contestSection.setVisibility(View.GONE);
        contestItem.setVisibility(View.GONE);


        setupFirebaseAuth();
        getImage();

    }


    private void getImage() {
        intent = getIntent();
        image = findViewById(R.id.imageShare);
        mcaption = findViewById(R.id.imgCaption);


        if (intent.hasExtra("image_shared")) {


            imgUrl = intent.getStringExtra("image_shared");

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
            imageLoader.displayImage(imgUrl, image);
            // Share Picture
            mFirebaseMethods = new FirebaseMethods(NextActivity.this);

            if (isMediaVideo(imgUrl)){

                relLayout3.setVisibility(View.VISIBLE);
                contestSection.setVisibility(View.VISIBLE);
                contestItem.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.GONE);
//                setupListView();

                chooseThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    }
                });

            }else{
                    shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                    Toast.makeText(NextActivity.this, "Image is being shared...", Toast.LENGTH_SHORT).show();
                    caption = mcaption.getText().toString();

                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null);
                    }
                });
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();

                final Uri thumbPath = data.getData();
                Glide
                        .with(this)
                        .load(thumbPath)
                        .apply(new RequestOptions()
                                .fitCenter()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .placeholder(R.drawable.loading_image))
                        .into(thumbnail);

                delhiContestItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (thumbPath!= null){
                            Intent intent = new Intent(NextActivity.this, activity_profile.class);
                            startActivity(intent);
                            finish();
                            TextView category = findViewById(R.id.delhiCategory);
                            Toast.makeText(NextActivity.this, "Video is being shared to \n delhi Contest..", Toast.LENGTH_SHORT).show();
                            caption = mcaption.getText().toString();
                            String getPathFromUri = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                getPathFromUri = ConvertUriToString.getPathFromUri(NextActivity.this, thumbPath);
                            }else{
                                Toast.makeText(NextActivity.this, "You should have atLeast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                            }

                            mFirebaseMethods.addNewVideoToStorage(getString(R.string.new_video),
                                    caption, videoCount, imgUrl, getPathFromUri, category.getText().toString());

                        }else{
                            Toast.makeText(NextActivity.this, "You Should Choose a Thumbnail photo to continue...",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                miContestItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (thumbPath!= null){
                            Intent intent = new Intent(NextActivity.this, activity_profile.class);
                            startActivity(intent);
                            finish();
                            TextView category = findViewById(R.id.miCategory);
                            Toast.makeText(NextActivity.this, "Video is being shared to \n delhi Contest..", Toast.LENGTH_SHORT).show();
                            caption = mcaption.getText().toString();
                            String getPathFromUri = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                getPathFromUri = ConvertUriToString.getPathFromUri(NextActivity.this, thumbPath);
                            }else{
                                Toast.makeText(NextActivity.this, "You should have atleast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                            }

                            mFirebaseMethods.addNewVideoToStorage(getString(R.string.new_video),
                                    caption, videoCount, imgUrl, getPathFromUri, category.getText().toString());
                        }else{
                            Toast.makeText(NextActivity.this, "You Should Choose a Thumbnail photo to continue...",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


                vivoContestItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (thumbPath!= null){
                            Intent intent = new Intent(NextActivity.this, activity_profile.class);
                            startActivity(intent);
                            finish();
                            TextView category = findViewById(R.id.vivoCategory);
                            Toast.makeText(NextActivity.this, "Video is being shared to \n delhi Contest..", Toast.LENGTH_SHORT).show();
                            caption = mcaption.getText().toString();
                            String getPathFromUri = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                getPathFromUri = ConvertUriToString.getPathFromUri(NextActivity.this, thumbPath);
                            }else{
                                Toast.makeText(NextActivity.this, "You should have atleast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                            }

                            mFirebaseMethods.addNewVideoToStorage(getString(R.string.new_video),
                                    caption, videoCount, imgUrl, getPathFromUri, category.getText().toString());
                        }else{
                            Toast.makeText(NextActivity.this, "You Should Choose a Thumbnail photo to continue...",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


                oppoContestItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (thumbPath!= null){
                            Intent intent = new Intent(NextActivity.this, activity_profile.class);
                            startActivity(intent);
                            finish();
                            TextView category = findViewById(R.id.oppoCategory);
                            Toast.makeText(NextActivity.this, "Video is being shared to \n delhi Contest..", Toast.LENGTH_SHORT).show();
                            caption = mcaption.getText().toString();
                            String getPathFromUri = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                getPathFromUri = ConvertUriToString.getPathFromUri(NextActivity.this, thumbPath);
                            }else{
                                Toast.makeText(NextActivity.this, "You should have atleast kitkat version to use this.", Toast.LENGTH_SHORT).show();
                            }

                            mFirebaseMethods.addNewVideoToStorage(getString(R.string.new_video),
                                    caption, videoCount, imgUrl, getPathFromUri, category.getText().toString());
                        }else{
                            Toast.makeText(NextActivity.this, "You Should Choose a Thumbnail photo to continue...",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }






    private boolean isMediaVideo(String uri){
        if(uri.contains(".mp4") || uri.contains(".wmv") || uri.contains(".flv") || uri.contains(".avi")){
            return true;
        }
        return false;
    }

     /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot!= null){
                    imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                    videoCount = mFirebaseMethods.getVideoCount(dataSnapshot);
                    Log.d(TAG, "onDataChange: image count: " + imageCount);
                    Log.d(TAG, "onDataChange: video count: " + videoCount);

                }else{
                    Toast.makeText(NextActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}




























//    public void setupListView(){
//
//        final ArrayList<Contest> contestListArray = new ArrayList<>();
////        final ArrayList<Contest> descriptions= new ArrayList<>();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//        Query query = reference
//                .child(getString(R.string.db_contest));
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
////                    ContestDescription description = new ContestDescription();
//                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
//
////                    description.setCategory(singleSnapshot.getValue(ContestDescription.class).getCategory());
////                    description.setCover_image(singleSnapshot.getValue(ContestDescription.class).getCover_image());
////                    description.setEnd_date(singleSnapshot.getValue(ContestDescription.class).getEnd_date());
////                    description.setStart_date(singleSnapshot.getValue(ContestDescription.class).getStart_date());
////                    description.setReward(singleSnapshot.getValue(ContestDescription.class).getReward());
//
//                    List<ContestDescription> mDescription = new ArrayList<ContestDescription>();
//
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_description)).getChildren()){
//                        ContestDescription description = new ContestDescription();
//                        description.setCategory(singleSnapshot.getValue(ContestDescription.class).getCategory());
//                        description.setCover_image(singleSnapshot.getValue(ContestDescription.class).getCover_image());
//                        description.setEnd_date(singleSnapshot.getValue(ContestDescription.class).getEnd_date());
//                        description.setStart_date(singleSnapshot.getValue(ContestDescription.class).getStart_date());
//                        description.setReward(singleSnapshot.getValue(ContestDescription.class).getReward());
//
//                        mDescription.add(description);
//                    }
//                    Contest  newDescription = new Contest();
//
//                    newDescription.setContestDescription(mDescription);
//                    contestListArray.add(newDescription);
//
////                    ArrayList<ContestDescription> allData = new ArrayList<ContestDescription>();
//                    ArrayList<Contest> allData = new ArrayList<Contest>();
//                    for (int i = 0; i < contestListArray.size(); i++) {
//                        allData.add(contestListArray.get(i));
//                    }
//
//                    ContestItemListAdapter adapter = new ContestItemListAdapter(NextActivity.this,
//                            R.layout.layout_grid_imageview,
//                             allData);
//                    contestList.setAdapter(adapter);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: query cancelled.");
//            }
//
//
//        });
//
//    }



//    private void setupListView() {
//        rowItems = new ArrayList<ContestDescription>();
////        for (int i = 0; i < category.length; i++) {
////            ContestDescription item = new ContestDescription(category[i], reward[i], start_date[i],
////                    end_date[i], images[i].toString() );
////
////            rowItems.add(item);
////        }
//        ArrayList<ContestDescription> item = new ArrayList<ContestDescription>();
//        for (int i = 0; i < category.length; i++) {
//            ContestDescription description = new ContestDescription(category[i], reward[i], start_date[i],
//                    end_date[i], images[i].toString());
//            item.add(description);
////            Toast.makeText(this, "items " + item.get(i), Toast.LENGTH_LONG).show();
//
//
////            Toast.makeText(this, "items " + category.length, Toast.LENGTH_LONG).show();
//
//            ContestItemListAdapter adapter = new ContestItemListAdapter(this,
//                    R.layout.snippet_contest_item_delhi_contest, item);
//            contestList.setAdapter(adapter);
//        }
//
//    }
