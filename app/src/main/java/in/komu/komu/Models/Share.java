package in.komu.komu.Models;

public class Share {

    private String user_id;

    public Share(String user_id) {
        this.user_id = user_id;
    }
    public Share() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Share{" +
                "user_id='" + user_id + '\'' +
                '}';
    }
}
