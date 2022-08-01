package com.example.daydayapp.fragments.leaderboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private final String TAG = "MyScoreTab";

    private final MainActivity main;
    private int score;
    private int fullScore;
    private int competeUserScore;
    private String competeUserName;

    private ProgressBar scoreBar,
                        competeBar;
    private TextView textViewScore,
                     competeUserNameTv,
                     compete_me_score,
                     compete_other_score;
    private Button competeButton;
    private ConstraintLayout competeLayout;

    private RequestQueue queue;

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
        queue = Volley.newRequestQueue(requireActivity());
        scoreBar = requireView().findViewById(R.id.myScore);
        competeBar = requireView().findViewById(R.id.competeBar);
        textViewScore = requireView().findViewById(R.id.text_view_progress);
        competeButton = requireView().findViewById(R.id.compete_button);
        competeLayout = requireView().findViewById(R.id.competeLayout);
        competeUserNameTv = requireView().findViewById(R.id.compete_other);
        compete_me_score = requireView().findViewById(R.id.score_me);
        compete_other_score = requireView().findViewById(R.id.score_other);

        // TODO: get compete user name, score
//        competeUserName = "Victor Pei";
//        competeUserScore = 20;
//        compete_other_score.setText(String.valueOf(competeUserScore));
//        competeUserNameTv.setText(competeUserName);

        if (competeUserName == null || competeUserName.trim().isEmpty()) {
            competeLayout.setVisibility(View.GONE);
        } else {
            competeButton.setVisibility(View.GONE);
            competeUserNameTv.setText(competeUserName);
        }

        competeButton.setOnClickListener(v -> {
            competeUserName = "Victor Pei";
            competeUserScore = 20;
            compete_other_score.setText(String.valueOf(competeUserScore));
            competeUserNameTv.setText(competeUserName);
            setScore(score);

            // TODO: change sleep to request
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            competeButton.setVisibility(View.GONE);
            competeLayout.setVisibility(View.VISIBLE);
        });

        displayScore();
    }

    public void setScore (int score) {
        this.score = score;
        if (scoreBar != null) {
            setProgress(score);
        }
        if (textViewScore != null) {
            textViewScore.setText(String.valueOf(score));
        }
        if (compete_me_score != null) {
            compete_me_score.setText(String.valueOf(score));
        }
        if (competeBar != null) {
            setCompeteProgress(score);
        }
    }

    private void displayScore() {
        final String url = "http://13.89.36.134:8000/user";
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "Successful");
                        score = (int) response.get("score");
                        setProgress(score);
                        textViewScore.setText(String.valueOf(score));
                        compete_me_score.setText(String.valueOf(score));
                        setCompeteProgress(score);
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

    private void setCompeteProgress(int score) {
        int totalScore = score + competeUserScore;
        competeBar.setProgress((score * 100) / totalScore);
    }

    private void setProgress(int score) {
        // get all tasks
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