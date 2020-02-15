package in.komu.komu.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class user_account_setting implements Parcelable{
    private String user_id;
    private String username;
    private long phone_number;
    private String email;

    public user_account_setting(String user_id, String username, long phone_number, String email) {
        this.user_id = user_id;
        this.username = username;
        this.phone_number = phone_number;
        this.email = email;
    }

    public user_account_setting( ) {
    }

    protected user_account_setting(Parcel in) {
        user_id = in.readString();
        username = in.readString();
        phone_number = in.readLong();
        email = in.readString();
    }

    public static final Creator<user_account_setting> CREATOR = new Creator<user_account_setting>() {
        @Override
        public user_account_setting createFromParcel(Parcel in) {
            return new user_account_setting(in);
        }

        @Override
        public user_account_setting[] newArray(int size) {
            return new user_account_setting[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "user_account_setting{" + "user_id='" + user_id + '\'' + ", username='" + username + '\'' + ", phone_number=" + phone_number + ", email='" + email + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeLong(phone_number);
        dest.writeString(email);
    }
}
