package com.example.daydayapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.daydayapp.MainActivity;
import com.example.daydayapp.fragments.leaderboard.FriendsTab;
import com.example.daydayapp.fragments.leaderboard.GlobalTab;
import com.example.daydayapp.fragments.leaderboard.LeaderboardFragment;
import com.example.daydayapp.fragments.leaderboard.MyScoreTab;

public class VPAdapter extends FragmentStateAdapter {

//    private final String[] titles = {"MyScore", "Friends", "Global"};
    private final MyScoreTab myScoreTab;
    private final GlobalTab globalTab;
    private final FriendsTab friendsTab;

    public VPAdapter(@NonNull LeaderboardFragment fragmentActivity, MainActivity main) {
        super(fragmentActivity);
        this.myScoreTab = new MyScoreTab(main);
        this.globalTab = new GlobalTab(main);
        this.friendsTab = new FriendsTab(main);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return this.friendsTab;
            case 2:
               return this.globalTab;
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
