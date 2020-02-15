package in.komu.komu.share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.security.Permission;

import in.komu.komu.Home.CameraFragment;
import in.komu.komu.R;
import in.komu.komu.Utils.Permissions;
import in.komu.komu.Utils.SectionsPagerAdapter;

public class ShareActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";


    private static final int REQUEST_CODE = 1;
    private ViewPager viewPager;

    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        
        if(checkPermissionArray(Permissions.PERMISSIONS)){
            setupViewPager();

        }else{
            verifyPermission(Permissions.PERMISSIONS);
        }


    }


    // ************* Setup ViewPager **********************//

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.viewpager_container);

        adapter.addFragment(new GalleryFragment());
//        adapter.addFragment(new CameraRollFragment());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.all_media));
//        tabLayout.getTabAt(1).setText(getString(R.string.videos));


    }

    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }




    //***************** Verifying Permission ***********************//



    private void verifyPermission(String[] permissions) {

        Log.d(TAG, "verifyPermission: Verifying Permissions");
        // Request Code can be Any integer

        ActivityCompat.requestPermissions(ShareActivity.this, permissions, REQUEST_CODE);
    }


    private boolean checkPermissionArray(String[] permissions) {

        Log.d(TAG, "checkPermissionArray: Checking Permission For all strings in Array");

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkpermission(check)) {
                return false;
            }
        }

        return true;
    }


    private boolean checkpermission(String permission) {

        Log.d(TAG, "checkpermission: Checking every single Permission.");
        int permissionRequest = ActivityCompat.checkSelfPermission(mContext, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkpermission: Permission not Granted.");
            return false;
        }else{
            Log.d(TAG, "checkpermission: Permission Granted Succesfully.");
            return true;
        }

    }


}
