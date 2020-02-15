
package in.komu.komu.Authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.komu.komu.MainActivity;
import in.komu.komu.R;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth mAuth;
    public DatabaseReference mDatabase;
    private Button noAccountYet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.email_login);

        mPasswordView = findViewById(R.id.password_login);


        // grab an instance of firebase auth
        setupFirebaseAuth();

        // No account yet button

        initButton();

        /// end button account yet
        mDatabase = FirebaseDatabase.getInstance().getReference().child("userSettings");

       init();
    }

    private void  initButton(){
        noAccountYet =  findViewById(R.id.noAccountYet);
        noAccountYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    /*---------------------Firebase Stuff ---------------------------*/

    private Context mContext = LoginActivity.this;
    private FirebaseAuth.AuthStateListener mAuthListener;



    private boolean isStringnull(String string){
        if (string.equals("")) {
            return true;
        }else {
            return false;
        }

    }
    private void init(){
        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", "Attempt to login");
                String mEmail = mEmailView.getText().toString();
                String mPassword = mPasswordView.getText().toString();

                if (isStringnull(mEmail) || isStringnull(mPassword)) {
                    Toast.makeText(mContext, "You should fill the required field.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Try to Log In", Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user.isEmailVerified()) {

                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("taskSuccess", "signInWithEmail:success");
                                            Toast.makeText(LoginActivity.this, "Authentication successfull.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
//                                        Toast.makeText(mContext, "Not Working ", Toast.LENGTH_SHORT).show();
                                            try {
//                                                if (user.isEmailVerified()) {
//                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                } else {
//                                                    Toast.makeText(mContext, "Email is not verified \n Please check Inbox.", Toast.LENGTH_SHORT).show();
//
//                                                    // mAuth.signOut();
//                                                }
                                                Toast.makeText(mContext, "Email Id or Password is incorrect \n " +
                                                        "Or you may not connected to network \n Please try again...", Toast.LENGTH_SHORT).show();
                                            } catch (NullPointerException e) {
                                                Log.e("komu", "Null Pointer Exception");
                                            }
                                        }
                                    }else {
                                        Toast.makeText(mContext, "Email is not verified \n Please check Inbox.", Toast.LENGTH_SHORT).show();
                                         mAuth.signOut();
                                    }
                                }
                            })

                    .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Email Id  or Password is Incorrect \n" +
                                    "Or You Are Not Connected To Internet ", Toast.LENGTH_LONG).show();
                        }
                    })
                    ;
                }


            }
        });
        if (mAuth.getCurrentUser() != null){
            Log.d("komu", "Now we are going to mainActivity");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();
                //Check if the current user is logged in

                if (user!= null){
                    Log.d("komu", "onAuthStateSignedIn : User is logged In" + user.getUid());
                }else {
                    Log.d("komu", "onAuthStateSignedIn : User is logged out");

                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);

    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}


















