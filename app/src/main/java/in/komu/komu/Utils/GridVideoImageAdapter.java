package in.komu.komu.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;

import in.komu.komu.R;

import static com.bumptech.glide.Glide.with;


public class GridVideoImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> videoUrls;
    private ArrayList<String> bitmapString;
    Bitmap bitmap;
//    private SquareImageView mSquareImageView;

    public GridVideoImageAdapter(Context context, int layoutResource, String append, ArrayList<String> videoUrls) {
        super(context, layoutResource, videoUrls);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
//        this.videoUrls = videoUrls;

        for (int i = 0; i<videoUrls.size(); i++){
            String singleUrl = videoUrls.get(i);
            bitmap = ThumbnailUtils.createVideoThumbnail(singleUrl, 0);

            bitmapString.add(bitmap.toString());
        }

    }

    private static class ViewHolder{
        SquareImageView mSquareImageView;
        ProgressBar mProgressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        Viewholder build pattern (Similar to recyclerview)
         */
        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);

            holder = new ViewHolder();
//            holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.gridImageProgressbar);
            holder.mSquareImageView = (SquareImageView) convertView.findViewById(R.id.gridSquareImageView);
//

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String videoUrl = getItem(position);
//
//            bitmap = ThumbnailUtils.createVideoThumbnail(videoUrl, 0);
//
        Glide
                .with(getContext())
                .asBitmap()
                .load(videoUrl)
                .apply(new RequestOptions()
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.loading_image))
                .into(holder.mSquareImageView);


//        Toast.makeText(mContext, "Position for video"+ videoUrl, Toast.LENGTH_SHORT).show();
//        try {
//            bitmap = retriveVideoFrameFromVideo(videoUrl);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        if (bitmap != null) {
//            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);
//            Glide
//                    .with(getContext())
//                    .asBitmap()
//                    .load(bitmap)
//                    .apply(new RequestOptions()
//                            .fitCenter()
//                            .centerCrop()
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                            .placeholder(R.drawable.loading_image))
//                    .into(holder.mSquareImageView);
//        }




//        String imgURL = getItem(position);

//        mSquareImageView = convertView.findViewById(R.id.gridSquareImageView);



        // It Takes LOt Of time
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));


        return convertView;
    }

//    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
//            throws Throwable {
//        Bitmap bitmap = null;
//        MediaMetadataRetriever mediaMetadataRetriever = null;
//        try {
//            mediaMetadataRetriever = new MediaMetadataRetriever();
//            if (Build.VERSION.SDK_INT >= 14)
//                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
//            else
//                mediaMetadataRetriever.setDataSource(videoPath);
//
//            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new Throwable(
//                    "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
//
//        } finally {
//            if (mediaMetadataRetriever != null) {
//                mediaMetadataRetriever.release();
//            }
//        }
//        return bitmap;
//    }
}



















