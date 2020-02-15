package in.komu.komu.share;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import in.komu.komu.Profile.EditProfileFragment;
import in.komu.komu.Profile.activity_accountSetting;
import in.komu.komu.R;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";

//    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    private ArrayList<String> imgURls;
    private String mSelectedImage;
    private Context mContext;
    private GridView gridView;
    private String mAppend = "file:/";
    private ImageView imageShared;
    private Button next_activity;
    private String imgURL;


    private ProgressBar mProgressBar;
//
//
//    //widgets
//    private GridView gridView;
//    private ProgressBar mProgressBar;
//    private Spinner directorySpinner;

    //vars
    private MediaAdapter mediaAdapter;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_media, container, false);

        //mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //mProgressBar.setVisibility(View.GONE);
//        directories = new ArrayList<>();
//        Log.d(TAG, "onCreateView: started.");

        mContext = getActivity();

        // Adapter
        mediaAdapter = new MediaAdapter(getActivity(), R.layout.layout_grid_imageview);

        // ImageSearch Class Object
        ImageSearch imageSearch = new ImageSearch();
        imgURls =  imageSearch.getAllShownImagesPath(getActivity());

        // ProgressBar
        mProgressBar = getActivity().findViewById(R.id.progressBar);

        // GridView Setup
        gridView =  view.findViewById(R.id.galleryGridView);
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int image_width = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(image_width);
        gridView.setAdapter(mediaAdapter);

        // OnClick Image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {

//                final String imgURL_appended = String.valueOf(mAppend + imgURls.get(position));
//                final String imgUrl = imgURls.get(position);

//                Intent i = new Intent(mContext, OpenGalleryImage.class);
//                i.putExtra("image_shared", imgURL_appended);
//                startActivity(i);
//


                final String imgURL_appended = String.valueOf(mAppend + imgURls.get(position));
                final String imgUrl = imgURls.get(position);

                if (isRootTask()){
                    Intent i = new Intent(mContext, OpenGalleryImage.class);
                    i.putExtra("image_shared", imgURL_appended);
                    ActivityOptions.makeCustomAnimation(mContext, R.anim.fade_in, R.anim.fade_out);
                    startActivity(i);
//                    imageShared.setVisibility(View.GONE);
//                    next_activity.setVisibility(View.GONE);

                }else{
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
//                    imageShared = getActivity().findViewById(R.id.image_shared);
//                    imageShared.setVisibility(View.VISIBLE);
                    //                Toast.makeText(mContext, "Successfully Clicked " + mAppend +imgURls.get(position), Toast.LENGTH_SHORT).show();
//                    imageLoader.displayImage(String.valueOf(mAppend + imgURls.get(position)), imageShared);

                    Intent i = new Intent(mContext, EditProfileFragment.class);
                    i.putExtra("image_shared", imgUrl);
                    i.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(i);
                    getActivity().finish();
                }

//
//                // Image Loader
//
                // Button For Next Activity
//
//                next_activity = getActivity().findViewById(R.id.next_activity);
//                next_activity.setVisibility(View.VISIBLE);
//                next_activity.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//
//                    }
//                });
//
//
////                if (null != imgURls && !imgURls.isEmpty()){
////
////                    Toast.makeText(mContext, "Successfully Clicked " + mAppend +imgURls.get(position), Toast.LENGTH_SHORT).show();
////
////                    mSelectedImage = String.valueOf(mAppend + imgURls.get(position));
////                    Intent intent = new Intent(getActivity(), OpenGalleryImage.class);
////                    intent.putExtra("image", mSelectedImage);
////                    startActivity(intent);
////
////                }else {
////                    Toast.makeText(mContext, "Null Pointer Exception." + imgURls.get(position), Toast.LENGTH_SHORT).show();
////                }


            }
        });


        //***********************************//
//        mediaAdapter.openImages();
//        public void openImages(){
//

//
//        }

        //init();

        return view;
    }



    private boolean isRootTask() {
        if (((ShareActivity) mContext).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }
}


//    private void init(){
//        FilePaths filePaths = new FilePaths();
//
//        //check for other folders inside "/storage/emulated/0/pictures"
//        if (ImageSearch.getDirectPathArray(filePaths.PICTURES) != null) {
//            directories = ImageSearch.getDirectPathArray(filePaths.PICTURES);
//        }
//        directories.add(filePaths.CAMERA);
//
//        ArrayList<String> directoryNames = new ArrayList<>();
//        for (int i = 0; i < directories.size(); i++) {
//            Log.d(TAG, "init: directory: " + directories.get(i));
//            int index = directories.get(i).lastIndexOf("/");
//            String string = directories.get(i).substring(index);
//            directoryNames.add(string);
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
//                android.R.layout.simple_spinner_item, directoryNames);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        directorySpinner.setAdapter(adapter);
//
//        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG, "onItemClick: selected: " + directories.get(position));
//
//                //setup our image grid for the directory chosen
//                //setupGridView(directories.get(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//    }



//************************    Method We Use Later *********************//////////////////////////////////

//        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
//        shareClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: closing the gallery fragment.");
//                mContext.finish();
//            }
//        });


//TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
//        nextScreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to the final share screen.");
//
//                if(isRootTask()){
//                    Intent intent = new Intent(mContext, NextActivity.class);
//                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
//                    startActivity(intent);
//                }else{
//                    Intent intent = new Intent(mContext, activity_accountSetting.class);
//                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
//                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
//                    startActivity(intent);
//                    mContext.finish();
//                }
//
//            }
//        });























