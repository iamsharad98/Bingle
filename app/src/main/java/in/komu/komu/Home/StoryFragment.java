package in.komu.komu.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Search.SearchActivity;
import in.komu.komu.Utils.SectionsPagerAdapter;

public class StoryFragment extends Fragment{


    private static final int REQUEST_CODE = 1;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //Widgets
    private ImageView profile;
    private TextView search;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_story, container, false);

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

        setupViewPager();


        return view;
    }

    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new FragmentDiscoverVideo());
        adapter.addFragment(new FragmentContest());

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.fragment_trending));
        tabLayout.getTabAt(1).setText(getString(R.string.fragment_contest));
    }
}
