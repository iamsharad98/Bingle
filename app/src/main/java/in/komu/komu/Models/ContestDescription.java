package in.komu.komu.Models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ContestDescription  implements Parcelable{
    private String category;
    private String reward;
    private String start_date;
    private String end_date;
    private String cover_image;

    public ContestDescription(String category, String reward, String start_date, String end_date, String cover_image) {
        this.category = category;
        this.reward = reward;
        this.start_date = start_date;
        this.end_date = end_date;
        this.cover_image = cover_image;
    }

    public ContestDescription() {
    }

    protected ContestDescription(Parcel in) {
        category = in.readString();
        reward = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        cover_image = in.readString();
    }

    public static final Creator<ContestDescription> CREATOR = new Creator<ContestDescription>() {
        @Override
        public ContestDescription createFromParcel(Parcel in) {
            return new ContestDescription(in);
        }

        @Override
        public ContestDescription[] newArray(int size) {
            return new ContestDescription[size];
        }
    };



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    @Override
    public String toString() {
        return "ContestDescription{" +
                "category='" + category + '\'' +
                ", reward='" + reward + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", cover_image='" + cover_image + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(reward);
        dest.writeString(start_date);
        dest.writeString(end_date);
        dest.writeString(cover_image);
    }


}
