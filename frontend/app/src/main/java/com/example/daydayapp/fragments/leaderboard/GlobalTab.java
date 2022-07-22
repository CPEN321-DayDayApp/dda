package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daydayapp.adapter.LBAdapter;
import com.example.daydayapp.model.LBModel;
import com.example.daydayapp.R;

import java.util.ArrayList;
import java.util.List;

public class GlobalTab extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_globaltab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        List<LBModel> globalRankList = new ArrayList<>();
        LBAdapter lBAdapter = new LBAdapter(requireActivity());
        RecyclerView globalLBListRecyclerView = requireView().findViewById(R.id.globalLBListRecyclerView);
        globalLBListRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        globalLBListRecyclerView.setAdapter(lBAdapter);

        LBModel rank1 = new LBModel();
        rank1.setName("John");
        rank1.setRank(1);
        globalRankList.add(rank1);

        LBModel rank2 = new LBModel();
        rank2.setName("Clara");
        rank2.setRank(2);
        globalRankList.add(rank2);

        LBModel rank3 = new LBModel();
        rank3.setName("Victor");
        rank3.setRank(3);
        globalRankList.add(rank3);

        LBModel rank4 = new LBModel();
        rank4.setName("Someone");
        rank4.setRank(4);
        globalRankList.add(rank4);

        lBAdapter.setRank(globalRankList);
    }
}