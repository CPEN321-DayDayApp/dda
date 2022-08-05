package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.example.daydayapp.adapter.LBAdapter;
import com.example.daydayapp.model.LBModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsTab extends Fragment {
    private static final String TAG = "Flb";
    private final MainActivity main;
    private RequestQueue queue;

    public FriendsTab(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friendstab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(requireActivity());
        List<LBModel> friendRankList = new ArrayList<>();
        LBAdapter lBAdapter = new LBAdapter(requireActivity());
        RecyclerView friendLBListRecyclerView = requireView().findViewById(R.id.friendLBListRecyclerView);
        friendLBListRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        friendLBListRecyclerView.setAdapter(lBAdapter);

        Timer refreshFriendsLB = new Timer();
        refreshFriendsLB.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //TODO: change url
                final String url = "http://13.89.36.134:8000/leaderboard/friend";
                HashMap<String, String> content = new HashMap<>();
                JSONObject jsonContent = new JSONObject(content);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                        response -> {
                            Log.d(TAG, "Successful");
                            try {
                                //TODO: change get
                                JSONArray friendsLB = (JSONArray) response.get("friendboard");
                                JSONObject user;
                                friendRankList.clear();
                                for (int i = 0; i < friendsLB.length(); i++) {
                                    LBModel rank = new LBModel();
                                    user = (JSONObject) friendsLB.get(i);
                                    rank.setName(user.get("name").toString());
                                    rank.setRank((Integer) user.get("rank"));
                                    rank.setScore((Integer) user.get("score"));
                                    friendRankList.add(rank);
                                }
                                lBAdapter.setRank(friendRankList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Log.d(TAG, error.toString())) {
                    /**
                     * Passing some request headers
                     * Set API Key
                     */
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                queue.add(jsonRequest);
            }
        },0,30000);
    }
}