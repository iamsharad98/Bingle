package in.komu.komu.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class users_profile  implements Parcelable{
    private String username;
    private String display_name;
    private String website;
    private String user_id;
    private String profile_photo;
    private String origin;
    private String about;
    private long post;
    private long followers;
    private long following;

    public users_profile(String username, String display_name, String website, String user_id, String profile_photo, String origin, String about, long post, long followers, long following) {
        this.username = username;
        this.display_name = display_name;
        this.website = website;
        this.user_id = user_id;
        this.profile_photo = profile_photo;
        this.origin = origin;
        this.about = about;
        this.post = post;
        this.followers = followers;
        this.following = following;
    }

    public users_profile() {
    }

    protected users_profile(Parcel in) {
        username = in.readString();
        display_name = in.readString();
        website = in.readString();
        user_id = in.readString();
        profile_photo = in.readString();
        origin = in.readString();
        about = in.readString();
        post = in.readLong();
        followers = in.readLong();
        following = in.readLong();
    }

    public static final Creator<users_profile> CREATOR = new Creator<users_profile>() {
        @Override
        public users_profile createFromParcel(Parcel in) {
            return new users_profile(in);
        }

        @Override
        public users_profile[] newArray(int size) {
            return new users_profile[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    @Override
    public String toString() {
        return "users_profile{" + "username='" + username + '\'' + ", display_name='" + display_name + '\'' + ", website='" + website + '\'' + ", user_id='" + user_id + '\'' + ", profile_photo='" + profile_photo + '\'' + ", origin='" + origin + '\'' + ", about='" + about + '\'' + ", post=" + post + ", followers=" + followers + ", following=" + following + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(display_name);
        dest.writeString(website);
        dest.writeString(user_id);
        dest.writeString(profile_photo);
        dest.writeString(origin);
        dest.writeString(about);
        dest.writeLong(post);
        dest.writeLong(followers);
        dest.writeLong(following);
    }
}
