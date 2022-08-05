package com.example.daydayapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.model.FriendModel;
import com.example.daydayapp.R;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private static final String TAG = "NotifyFriends";
    private final MainActivity activity;
    private List<FriendModel> friendList;

    public FriendAdapter(FragmentActivity fragment){
        this.activity = (MainActivity) fragment;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendModel item = friendList.get(position);
        holder.name.setText(item.getName());
        holder.status.setText(item.getStatus());
        holder.notifyButton.setOnClickListener(v -> {
            if (Objects.equals(item.getStatus(), "STUDYING")) {
                Toast.makeText(getContext(), "Your friend is studying. Please DO NOT disturb.", Toast.LENGTH_LONG).show();
            } else {
                friendNotification(activity.getAccount().getIdToken(), item.getEmail());
            }
        });
    }

    public void friendNotification(String token, String email) {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        final String url = "http://13.89.36.134:8000/pn" ;
        HashMap<String, String> content = new HashMap<>();
        content.put("email", email);
        JSONObject jsonContent = new JSONObject(content);
        final String mRequestBody = jsonContent.toString();
        Log.d(TAG, "/pn Request body: " + mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            Log.e(TAG, email);
            Toast.makeText(getContext(), "Notification sent", Toast.LENGTH_SHORT).show();
        }, error -> {
            Log.e(TAG, error.toString());
            Toast.makeText(getContext(), "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return mRequestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    public int getItemCount(){
        if (friendList == null) return 0;
        return friendList.size();
    }

    public void setFriends(List<FriendModel> friendList) {
        this.friendList = friendList;
        notifyDataSetChanged();
    }

    public void setFriends() {
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        friendList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,
                 status;
        Button notifyButton;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.friend_name);
            status = view.findViewById(R.id.friend_status);
            notifyButton = view.findViewById(R.id.friend_notify_button);
        }
    }

    public FriendModel getFriend(int position) {
        return friendList.get(position);
    }
}
