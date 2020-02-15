package in.komu.komu.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.komu.komu.R;

public class GridImageAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    private String mAppend;
    private ArrayList<String> imgURLs;
//    private SquareImageView mSquareImageView;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imgURLs) {
        super(context, layoutResource, imgURLs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;
        this.layoutResource = layoutResource;
        mAppend = append;
        this.imgURLs = imgURLs;
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

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        String imgURL = getItem(position);

//        mSquareImageView = convertView.findViewById(R.id.gridSquareImageView);

        Glide
                .with(getContext())
                .load(imgURL)
                .apply(new RequestOptions()
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.loading_image))

                .into(holder.mSquareImageView);

        // It Takes LOt Of time
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));


        return convertView;
    }
}



















