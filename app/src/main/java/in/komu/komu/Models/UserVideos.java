package in.komu.komu.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserVideos implements Parcelable{


    private ArrayList<Video> media;
    private users_profile users_profile;

    public UserVideos() {
    }

    public UserVideos(ArrayList<Video> media, in.komu.komu.Models.users_profile users_profile) {
        this.media = media;
        this.users_profile = users_profile;
    }

    protected UserVideos(Parcel in) {
        users_profile = in.readParcelable(in.komu.komu.Models.users_profile.class.getClassLoader());
    }

    public static final Creator<UserVideos> CREATOR = new Creator<UserVideos>() {
        @Override
        public UserVideos createFromParcel(Parcel in) {
            return new UserVideos(in);
        }

        @Override
        public UserVideos[] newArray(int size) {
            return new UserVideos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(users_profile, flags);
    }

    public ArrayList<Video> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<Video> media) {
        this.media = media;
    }

    public in.komu.komu.Models.users_profile getUsers_profile() {
        return users_profile;
    }

    public void setUsers_profile(in.komu.komu.Models.users_profile users_profile) {
        this.users_profile = users_profile;
    }

    @Override
    public String toString() {
        return "UserVideos{" +
                "media=" + media +
                ", users_profile=" + users_profile +
                '}';
    }
}
