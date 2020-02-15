package in.komu.komu.Models;

import java.util.List;

import in.komu.komu.Utils.Like;

public class Notification {

    private String date_created;
    private String post_path;
    private String post_id;
    private String user_id;
    private String profile_photo;
    private String post_type;
    private String notification_type;
    private List<Like> likes;
    private List<Comment> comments;
    private List<Share> shares;

    public Notification() {

    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getPost_path() {
        return post_path;
    }

    public void setPost_path(String post_path) {
        this.post_path = post_path;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public List<Share> getShares() {
        return shares;
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "date_created='" + date_created + '\'' +
                ", post_path='" + post_path + '\'' +
                ", post_id='" + post_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", post_type='" + post_type + '\'' +
                ", notification_type='" + notification_type + '\'' +
                ", likes=" + likes +
                ", comments=" + comments +
                ", shares=" + shares +
                '}';
    }
}


