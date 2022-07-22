package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.example.daydayapp.adapter.VPAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class LeaderboardFragment extends Fragment {

    private VPAdapter vpAdapter;
    private final String[] titles = {"MyScore", "Friends", "Global"};
    private final MainActivity main;

    public LeaderboardFragment(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vpAdapter = new VPAdapter(this, main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        ViewPager2 viewPager2 = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tabLB);

        viewPager2.setAdapter(vpAdapter);
        viewPager2.setSaveEnabled(false);

        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles[position]))).attach();

        return view;
    }

    public MyScoreTab getMyScoreTab() {
        return vpAdapter.getMyScoreTab();
    }

}