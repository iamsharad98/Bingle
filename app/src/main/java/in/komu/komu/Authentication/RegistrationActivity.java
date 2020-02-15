package in.komu.komu.Authentication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;


public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmail;
    private EditText mName;
    private EditText mPassword;
    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mPleaseWaitText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private String append;
    public String username;
    public String email;
    public String password;
    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        append = "";
        initWidgets();
        mProgressBar.setVisibility(View.GONE);
        mPleaseWaitText.setVisibility(View.GONE);
        setupFirebaseAuth();
        init();

    }

    // ----------- initialise all widgets ----------//
    private void initWidgets(){

        mEmail = findViewById(R.id.email_register);
        mName = findViewById(R.id.name_register);
        mPassword = findViewById(R.id.password_register);
        mContext = RegistrationActivity.this;
        mProgressBar = findViewById(R.id.progressBar);
        mPleaseWaitText = findViewById(R.id.pleaseWaitText);
        firebaseMethods = new FirebaseMethods(mContext);

    }

    // ----------- Initialise the Button Register ----------- //

    private void init(){
        Button register = findViewById(R.id.registration);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                username = mName.getText().toString();
                if (checkInputs(username, email, password)) {
                    if (password.length() > 8) {


//                    mProgressBar.setVisibility(View.VISIBLE);
//                    mPleaseWaitText.setVisibility(View.VISIBLE);
                        firebaseMethods.registerNewEmail(email, password, username);
                    } else {
                        Toast.makeText(mContext, "Password is too short.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean checkInputs(String name, String email, String password){
        if (name.equals("") || email.equals("") || password.equals("") ){
            Toast.makeText(mContext, "You should fill the required field.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference("message");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = mAuth.getCurrentUser();
                //Check if the current user is logged in

                if (user!= null){
                    Log.d("komu", "onAuthStateSignedIn : User is logged In" + user.getUid());

                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // firstly checkOut if username already exist
                            for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                if (singleSnapshot.exists()){
//                                    Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                                    append = mRef.push().getKey().substring(3,10);
//                                    Log.d(TAG, "onDataChange: username already exists. Appending random string to name: " + append);
                                }
                            }

                            mUsername = "";
                            mUsername = username + append;
                            // add data to database
                            firebaseMethods.addNewUser(email, mUsername, "", "", "");
                            Toast.makeText(mContext,"Signup Successfully. Sending Verification Email.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            mAuth.signOut();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


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