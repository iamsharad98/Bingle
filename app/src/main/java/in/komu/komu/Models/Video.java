package in.komu.komu.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import in.komu.komu.Utils.Like;

public class Video implements Parcelable{

    private String user_id;
    private String timestamp;
    private String video_url;
    private String thumb_url;
    private String video_id;
    private String category;
    private String views;
    private String duration;
    private String caption;
    private String tags;
    private List<Like> likes;
    private List<Comment> comments;

    public Video() {
    }

    public Video(String user_id, String timestamp, String video_url, String thumb_url, String video_id,
                 String category, String views, String duration, String caption, String tags, List<Like> likes, List<Comment> comments) {
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.video_url = video_url;
        this.thumb_url = thumb_url;
        this.video_id = video_id;
        this.category = category;
        this.views = views;
        this.duration = duration;
        this.caption = caption;
        this.tags = tags;
        this.likes = likes;
        this.comments = comments;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    protected Video(Parcel in) {
        user_id = in.readString();
        timestamp = in.readString();
        video_url = in.readString();
        thumb_url = in.readString();
        video_id = in.readString();
        category = in.readString();
        views = in.readString();
        duration = in.readString();
        caption = in.readString();
        tags = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public String toString() {
        return "Video{" +
                "user_id='" + user_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", video_url='" + video_url + '\'' +
                ", thumb_url='" + thumb_url + '\'' +
                ", video_id='" + video_id + '\'' +
                ", category='" + category + '\'' +
                ", views='" + views + '\'' +
                ", duration='" + duration + '\'' +
                ", caption='" + caption + '\'' +
                ", tags='" + tags + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(timestamp);
        dest.writeString(video_url);
        dest.writeString(thumb_url);
        dest.writeString(video_id);
        dest.writeString(category);
        dest.writeString(views);
        dest.writeString(duration);
        dest.writeString(caption);
        dest.writeString(tags);
    }
}

