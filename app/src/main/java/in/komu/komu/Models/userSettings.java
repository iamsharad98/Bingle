package in.komu.komu.Models;

public class userSettings {
    private user_account_setting setting;
    private users_profile users_profile;

    public userSettings(user_account_setting setting, in.komu.komu.Models.users_profile users_profile) {
        this.setting = setting;
        this.users_profile = users_profile;
    }

    public userSettings() {

    }
    public user_account_setting getSetting() {
        return setting;
    }

    public void setSetting(user_account_setting setting) {
        this.setting = setting;
    }

    public in.komu.komu.Models.users_profile getUsers_profile() {
        return users_profile;
    }

    public void setUsers_profile(in.komu.komu.Models.users_profile users_profile) {
        this.users_profile = users_profile;
    }

    @Override
    public String toString() {
        return "userSettings{" +
                "setting=" + setting +
                ", users_profile=" + users_profile +
                '}';
    }
}
