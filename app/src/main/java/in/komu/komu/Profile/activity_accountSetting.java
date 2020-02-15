package in.komu.komu.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import in.komu.komu.Authentication.ChooseLoginRegistrationActivity;
import in.komu.komu.R;
import in.komu.komu.Utils.FirebaseMethods;
import in.komu.komu.Utils.SectionsStatePagerAdapter;

public class activity_accountSetting extends AppCompatActivity {
    private static final String TAG = "activity_accountSetting";


    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_layout);

        mContext = activity_accountSetting.this;
        Log.d(TAG, "onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        // Call the Methods
        //setupSettingsList();
//        setupFragments();
//        getIncomingIntent();

        //setup the backarrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });

        // Edit Profile Button
        final TextView editProfile = findViewById(R.id.tvEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfileMethod();
            }
        });
        // Logout Button

        final TextView logout = findViewById(R.id.tvLogOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutMethod();
            }
        });
    }

    private void editProfileMethod() {

        Intent intent = new Intent(activity_accountSetting.this, EditProfileFragment.class);
        startActivity(intent);
    }

    private void logoutMethod() {

        Intent intent = new Intent(activity_accountSetting.this, SignOutFragment.class);
        startActivity(intent);
    }


}




// ****************************** Account Setting fragment for future use *********************************

//
//    private void getIncomingIntent(){
//        Intent intent = getIntent();
//
//        if(intent.hasExtra("calling_activity")){
//            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
//            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
//        }
//    }
//
//    private void setupFragments() {
//        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
//        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //fragment 0
//        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment)); //fragment 1
//    }
//
//    public void setViewPager(int fragmentNumber) {
//        mRelativeLayout.setVisibility(View.GONE);
//        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
//        mViewPager.setAdapter(pagerAdapter);
//        mViewPager.setCurrentItem(fragmentNumber);
//    }

//    private void setupSettingsList() {
//        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
//        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);
//
//        ArrayList<String> options = new ArrayList<>();
//        options.add(getString(R.string.edit_profile_fragment)); //fragment 0
//        options.add(getString(R.string.sign_out_fragment)); //fragement 1
//
//        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
//        listView.setAdapter(adapter);
//
////        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Log.d(TAG, "onItemClick: navigating to fragment#: " + position);
////                setViewPager(position);
////            }
////        });
//
//    }