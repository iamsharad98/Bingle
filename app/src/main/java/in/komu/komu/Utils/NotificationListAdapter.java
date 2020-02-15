package in.komu.komu.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import in.komu.komu.Models.Comment;
import in.komu.komu.Models.Notification;
import in.komu.komu.Models.users_profile;
import in.komu.komu.R;

/**
 * Created by User on 8/22/2017.
 */

public class NotificationListAdapter extends ArrayAdapter<Notification> {

    private static final String TAG = "NotificationListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public NotificationListAdapter(@NonNull Context context, @LayoutRes int resource,
                                   @NonNull List<Notification> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView  username, timestamp, textNotification;
        CircleImageView profileImage;
        SquareImageView postImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timeStamp);
            holder.textNotification = (TextView) convertView.findViewById(R.id.textNotification);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_photo);
            holder.postImage = (SquareImageView) convertView.findViewById(R.id.postImage);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + " d");
        }else{
            holder.timestamp.setText("today");
        }

        // Set the post image
        Glide.with(getContext().getApplicationContext())
                .load(getItem(position).getPost_path())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .into(holder.postImage);


//        Toast.makeText(mContext, "Item " + getItem(position).getPost_path(), Toast.LENGTH_SHORT).show();


//
//        // set the Notification text
        if (getItem(position).getPost_type().equals(mContext.getString(R.string.photo_type))){
            if (getItem(position).getNotification_type().equals(mContext.getString(R.string.field_likes))){
                holder.textNotification.setText(mContext.getString(R.string.text_like_photo));
            }else if (getItem(position).getNotification_type().equals(mContext.getString(R.string.field_comment))){
                holder.textNotification.setText(mContext.getString(R.string.text_comment_photo));

            }

        }else if(getItem(position).getPost_type().equals(mContext.getString(R.string.video_type))){
            if (getItem(position).getNotification_type().equals(mContext.getString(R.string.field_likes))){
                holder.textNotification.setText(mContext.getString(R.string.text_like_video));
            }else if (getItem(position).getNotification_type().equals(mContext.getString(R.string.field_comment))){
                holder.textNotification.setText(mContext.getString(R.string.text_comment_video));

            }
        }

        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.db_users_profile))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    holder.username.setText(
                            singleSnapshot.getValue(users_profile.class).getUsername());

//                    ImageLoader imageLoader = ImageLoader.getInstance();
//                    imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
//                    imageLoader.displayImage(
//                            singleSnapshot.getValue(users_profile.class).getProfile_photo(),
//                            holder.profileImage);
                    Glide.with(getContext().getApplicationContext())
                            .load(singleSnapshot.getValue(users_profile.class).getProfile_photo())
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_profile_dark)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                            )
                            .into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });

//        try{
//            if(position == 0){
//                holder.like.setVisibility(View.GONE);
//                holder.likes.setVisibility(View.GONE);
//                holder.reply.setVisibility(View.GONE);
//            }
//        }catch (NullPointerException e){
//            Log.e(TAG, "getView: NullPointerException: " + e.getMessage() );
//        }


        return convertView;
    }

    /**
     * Returns a string representing the number of days ago the post was made
     * @return
     */

    private String getTimestampDifference(Notification notification){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = notification.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 )));
        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException: " + e.getMessage() );
            difference = "0";
        }
        return difference;
    }
}






























