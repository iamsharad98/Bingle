package in.komu.komu.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RelativeLayout;

import in.komu.komu.R;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    private AppCompatButton mLogin;
    private AppCompatButton mRegister;
    private RelativeLayout relLayout1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        mLogin = findViewById(R.id.chooseLogin);
        mRegister = findViewById(R.id.chooseRegister);
        relLayout1 = findViewById(R.id.relLayout1);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this,RegistrationActivity.class);
                startActivity(intent);

            }
        });

        hideSystemUi();

    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        relLayout1.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}
