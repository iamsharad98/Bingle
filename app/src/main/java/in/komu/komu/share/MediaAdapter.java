package in.komu.komu.share;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import in.komu.komu.R;


public class MediaAdapter extends BaseAdapter {

    /** The context. */
    private Activity context;
    private ArrayList<String> images;
    private Context mContext;
    private int layoutResource;



    /**
     * Instantiates a new image adapter.
     *
     * @param localContext
     *            the local context
     */
    public MediaAdapter(Activity localContext, int layoutResource) {
        context = localContext;
        this.layoutResource = layoutResource;
        ImageSearch fileSearch = new ImageSearch();
        images = fileSearch.getAllShownImagesPath(context);



    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ImageView picturesView;
        if (convertView == null) {
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            picturesView
                    .setLayoutParams(new GridView.LayoutParams(270, 270));

        } else {
            picturesView = (ImageView) convertView;
        }

        Glide.with(context).load(images.get(position))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter())
                .into(picturesView);

        return picturesView;
    }



}


















