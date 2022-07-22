package com.example.daydayapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.daydayapp.fragments.leaderboard.FriendsTab;
import com.example.daydayapp.fragments.leaderboard.GlobalTab;
import com.example.daydayapp.fragments.leaderboard.LeaderboardFragment;
import com.example.daydayapp.fragments.leaderboard.MyScoreTab;
import com.example.daydayapp.MainActivity;

public class VPAdapter extends FragmentStateAdapter {

//    private final String[] titles = {"MyScore", "Friends", "Global"};
    private final MyScoreTab myScoreTab;

    public VPAdapter(@NonNull LeaderboardFragment fragmentActivity, MainActivity main) {
        super(fragmentActivity);
        this.myScoreTab = new MyScoreTab(main);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return new FriendsTab();
            case 2:
               return new GlobalTab();
            default:
                return this.myScoreTab;
        }
    }

    @Override
    public int getItemCount() {
//        return titles.length;
        return 3;
    }

    public MyScoreTab getMyScoreTab() {
        return this.myScoreTab;
    }
}
