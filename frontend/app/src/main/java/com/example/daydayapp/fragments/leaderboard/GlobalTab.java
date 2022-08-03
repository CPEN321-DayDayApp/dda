package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.adapter.LBAdapter;
import com.example.daydayapp.model.LBModel;
import com.example.daydayapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalTab extends Fragment {
    private static final String TAG = "Glb";
    private final MainActivity main;
    private RequestQueue queue;
    private Timer refreshGlobalLB;

    public GlobalTab(MainActivity main) {
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
        return inflater.inflate(R.layout.fragment_globaltab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(requireActivity());
        List<LBModel> globalRankList = new ArrayList<>();
        LBAdapter lBAdapter = new LBAdapter(requireActivity());
        RecyclerView globalLBListRecyclerView = requireView().findViewById(R.id.globalLBListRecyclerView);
        globalLBListRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        globalLBListRecyclerView.setAdapter(lBAdapter);

        refreshGlobalLB = new Timer();
        refreshGlobalLB.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final String url = "http://13.89.36.134:8000/leaderboard/global";
                HashMap<String, String> content = new HashMap<>();
                JSONObject jsonContent = new JSONObject(content);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                        response -> {
                            Log.d(TAG, "Successful");
                            try {
                                JSONArray globalLB = (JSONArray) response.get("globalboard");
                                JSONObject user;
                                globalRankList.clear();
                                for (int i = 0; i < globalLB.length(); i++) {
                                    LBModel rank = new LBModel();
                                    user = (JSONObject) globalLB.get(i);
                                    rank.setName(user.get("name").toString());
                                    rank.setRank((Integer) user.get("globalrank"));
                                    Log.d(TAG, user.get("name").toString());
                                    globalRankList.add(rank);
                                }
                                lBAdapter.setRank(globalRankList);
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