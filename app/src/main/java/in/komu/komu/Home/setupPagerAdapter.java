package in.komu.komu.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class setupPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "setupPagerAdapter";
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public setupPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public  Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
}




















