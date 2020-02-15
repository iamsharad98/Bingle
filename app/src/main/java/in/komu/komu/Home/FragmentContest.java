package in.komu.komu.Home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.komu.komu.Models.ContestDescription;
import in.komu.komu.Profile.activity_profile;
import in.komu.komu.R;
import in.komu.komu.Utils.ContestItemListAdapter;
import in.komu.komu.share.ConvertUriToString;
import in.komu.komu.share.NextActivity;

public class FragmentContest extends Fragment {
    private static final String TAG = "FragmentDiscoverVideo";

    //vars
    private int ACTIVITY_NUM = 5;
    private int NUM_GRID_COLUMNS = 2;


    //widgets
    private RelativeLayout contestSection;
    private RelativeLayout delhiContestItem;
    private RelativeLayout miContestItem;
    private RelativeLayout oppoContestItem;
    private RelativeLayout vivoContestItem;


    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        assert inflater != null;
        View view = inflater.inflate(R.layout.fragment_contest, container, false);

//        contestList = findViewById(R.id.contestList);
        delhiContestItem = view.findViewById(R.id.delhiContestItem);
        miContestItem = view.findViewById(R.id.miContestItem);
        oppoContestItem = view.findViewById(R.id.oppoContestItem);
        vivoContestItem = view.findViewById(R.id.vivoContestItem);

        setupClick();


        return view;
    }



    private void setupClick(){
        delhiContestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "This is delhiContestTop100", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getContext(), ActivityContestGrid.class);
                intent.putExtra(getContext().getString(R.string.contest_type), "delhiContestTop!00");
                getContext().startActivity(intent);
            }
        });

        miContestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "This is miContest", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getContext(), ActivityContestGrid.class);
                intent.putExtra(getContext().getString(R.string.contest_type), "bingleMiContest");
                getContext().startActivity(intent);
            }
        });


        vivoContestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "This is vivoContest", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getContext(), ActivityContestGrid.class);
                intent.putExtra(getContext().getString(R.string.contest_type), "bingleVivoContest");
                getContext().startActivity(intent);

            }
        });


        oppoContestItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), "This is OppoContest", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(getContext(), ActivityContestGrid.class);
                intent.putExtra(getContext().getString(R.string.contest_type), "bingleOppof7Contest");
                getContext().startActivity(intent);

            }
        });

    }
}
