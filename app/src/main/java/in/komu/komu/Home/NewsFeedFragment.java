package in.komu.komu.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Search.SearchActivity;
import in.komu.komu.Utils.MainfeedListAdapter;
import in.komu.komu.Utils.MainfeedVideoListAdapter;
import in.komu.komu.Utils.SectionsPagerAdapter;

public class NewsFeedFragment extends Fragment implements
        MainfeedListAdapter.OnLoadMoreItemsListener,
        MainfeedVideoListAdapter.OnLoadMoreItemsListener{


    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        NewsFeedPhotoFragment fragment = (NewsFeedPhotoFragment)getChildFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + viewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
        NewsFeedVideoFragment fragment1 = (NewsFeedVideoFragment)getChildFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + viewPager.getCurrentItem());
        if(fragment1 != null){
            fragment1.displayMorePhotos();
        }
    }

    private static final String TAG = "NewsFeedFragment";
    private static final int REQUEST_CODE = 1;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView profile;
    private TextView search;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        viewPager = view.findViewById(R.id.viewpager_container);
        tabLayout = view.findViewById(R.id.topTabs);
        profile = view.findViewById(R.id.profile_icon);
        search = view.findViewById(R.id.etSearch);



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        setupViewPager();

        profile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(getContext(), activity_profile.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            }
        });


        return view;
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new NewsFeedPhotoFragment());
        adapter.addFragment(new NewsFeedVideoFragment());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.fragment_newsfeed_photo));
        tabLayout.getTabAt(1).setText(getString(R.string.fragment_newsfeed_video));
    }


}
