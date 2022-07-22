package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyScoreTab extends Fragment {

    private int score;
    private ProgressBar scoreBar;
    private TextView textViewScore;
    private final MainActivity main;
    private int fullScore;

    private final String TAG = "MyScoreTab";

    public MyScoreTab(MainActivity main) {
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
        return inflater.inflate(R.layout.fragment_myscoretab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        scoreBar = requireView().findViewById(R.id.myScore);
        textViewScore = requireView().findViewById(R.id.text_view_progress);

        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        final String url = "http://13.89.36.134:8000/user";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "Successful");
                        score = (int) response.get("score");
                        setProgress(score);
                        textViewScore.setText(String.valueOf(score));
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

    public void setScore (int score) {
        this.score = score;
        if (scoreBar != null) {
            setProgress(score);
        }
        if (textViewScore != null) {
            textViewScore.setText(String.valueOf(score));
        }
    }

    private void setProgress(int score) {
        // get all tasks
        RequestQueue queue = Volley.newRequestQueue(main);

        final String url = "http://13.89.36.134:8000/tdl";
        HashMap<String, String> content = new HashMap<>();
        JSONObject jsonContent = new JSONObject(content);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                response -> {
                    Log.d(TAG, "Successful");
                    try {
                        JSONArray tdl = (JSONArray) response.get("tasklist");
                        fullScore = getFullScore(tdl);
                        fullScore += score;
                        if (fullScore != 0) {
                            scoreBar.setProgress((score * 100) / fullScore);
                        } else {
                            scoreBar.setProgress(0);
                        }

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

    private int getFullScore(JSONArray taskList) throws JSONException {
        int fullScore = 0;
        JSONObject task;
        for (int i = 0; i < taskList.length(); i++) {
            task = (JSONObject) taskList.get(i);
            fullScore += (int) task.get("time") / 15;
        }
        return fullScore;
    }
}