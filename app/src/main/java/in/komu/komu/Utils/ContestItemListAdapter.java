package in.komu.komu.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import in.komu.komu.Models.ContestDescription;
import in.komu.komu.R;
import in.komu.komu.share.NextActivity;

public class ContestItemListAdapter extends ArrayAdapter<ContestDescription> {

    private static final String TAG = "ContestItemListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public ContestItemListAdapter(@NonNull Context context, @LayoutRes int resource, List<ContestDescription> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }




    private static class ViewHolder{
        TextView category, rewards, startDate, endDate;
        SquareImageView contestImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

//            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.rewards = (TextView) convertView.findViewById(R.id.reward);
            holder.startDate = (TextView) convertView.findViewById(R.id.startDate);
            holder.endDate = (TextView) convertView.findViewById(R.id.endDate);
            holder.contestImage = (SquareImageView) convertView.findViewById(R.id.contestImage);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        ContestDescription description = getItem(position);
        Toast.makeText(mContext, "items "+ description, Toast.LENGTH_SHORT).show();

        //set the comment
        holder.category.setText(description.getCategory());

        holder.startDate.setText(description.getStart_date());
        holder.endDate.setText(description.getEnd_date());
        holder.rewards.setText(description.getReward());

        Glide.with(getContext())
                .load(mContext.getResources()
                        .getIdentifier(description.getCover_image().replace("R.drawable.", ""), "drawable", mContext.getPackageName()))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .into(holder.contestImage);

        return convertView;
    }
}
