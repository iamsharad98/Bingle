package in.komu.komu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import in.komu.komu.Authentication.ChooseLoginRegistrationActivity;

public class SplashScreenActivity extends AppCompatActivity {

    public static Boolean started = false;
    private FirebaseAuth mAuth;

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
//                    FirebaseDatabase.getInstance().getReference().keepSynced(true);
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(getApplication(), ChooseLoginRegistrationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}

