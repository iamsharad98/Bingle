package in.komu.komu;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class komu extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
