package com.example.daydayapp.fragments.friends;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.adapter.FriendAdapter;
import com.example.daydayapp.enums.FriendStatus;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.model.FriendModel;
import com.example.daydayapp.R;
import com.example.daydayapp.recycleritemtouchhepler.FriendRecyclerItemTouchHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FriendsFragment extends Fragment {
    private static final String TAG = "Friends";
    private final MainActivity main;
    private TextView searchResultView;
    private AlertDialog dialog;
    private Timer refreshFriendList;

    private FriendAdapter friendAdapter;
    private HashMap<String, FriendModel> friendEmailMap;
    private String searchResult,
                   email,
                   friendId;
    private RequestQueue queue;

    public FriendsFragment(MainActivity main) {
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(requireActivity());
        friendEmailMap = new HashMap<>();
        RecyclerView friendListRecyclerView = requireView().findViewById(R.id.friendListRecyclerView);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        friendAdapter = new FriendAdapter(requireActivity());
        friendListRecyclerView.setAdapter(friendAdapter);
        ImageButton add_friend_button = view.findViewById(R.id.add_friend_button);
        add_friend_button.setOnClickListener(v -> createNewFriendDialog());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new FriendRecyclerItemTouchHelper(friendAdapter, main, friendEmailMap));
        itemTouchHelper.attachToRecyclerView(friendListRecyclerView);

        // Update friend list every 30 seconds to refresh friends' status
        refreshFriendList = new Timer();
        refreshFriendList.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                Log.d(TAG, "Get friend list");
                final String url = "http://13.89.36.134:8000/friend";
                HashMap<String, String> content = new HashMap<>();
                JSONObject jsonContent = new JSONObject(content);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                        response -> {
                            Log.d(TAG, "Successful");
                            try {
                                JSONArray friendsInfo = (JSONArray) response.get("friendlist");
                                ArrayList<String> old_friendList = new ArrayList<>(friendEmailMap.keySet());
                                for (int i = 0; i < friendsInfo.length(); i++) {
                                    setFriendsList((JSONObject) friendsInfo.get(i), old_friendList);
                                }
                                // The remaining emails in old_friendList are the friends being deleted.
                                // Also delete them in the friend map
                                if (old_friendList.size() > 0) {
                                    for (String deletedEmail : old_friendList) {
                                        friendEmailMap.remove(deletedEmail);
                                    }
                                }
                                friendAdapter.setFriends(new ArrayList<>(friendEmailMap.values()));
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

    public void createNewFriendDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View newFriendPopupView = getLayoutInflater().inflate(R.layout.new_friend_popup, null);

        Button newFriendPopup_cancelButton = newFriendPopupView.findViewById(R.id.newFriendPopup_cancelButton);
        Button newFriendPopup_saveButton = newFriendPopupView.findViewById(R.id.newFriendPopup_saveButton);
        SearchView searchView = newFriendPopupView.findViewById(R.id.newFriendSearch);
        searchResultView = newFriendPopupView.findViewById(R.id.searchFriendResult);

        dialogBuilder.setView(newFriendPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        newFriendPopup_saveButton.setOnClickListener(v -> {
            if (email == null || email.equals("")) {
                Toast.makeText(main, "Please fill in email", Toast.LENGTH_SHORT).show();
            } else if (email.equals(main.getAccount().getEmail())) {
                Toast.makeText(main, "Cannot add yourself as friend!", Toast.LENGTH_SHORT).show();
            } else if (searchResult != null && !searchResult.equals("")) {
                final String url = "http://13.89.36.134:8000/friend/" + email;
                HashMap<String, String> content = new HashMap<>();
                content.put("name", searchResult);
                content.put("friendId", friendId);
                JSONObject jsonContent = new JSONObject(content);
                final String mRequestBody = jsonContent.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                    Log.i(TAG, response);
                    FriendModel friendtoAdd = new FriendModel();
                    friendtoAdd.setEmail(email);
                    //TODO: change status (get the friend's status or server send the status in response)
                    friendtoAdd.setStatus(FriendStatus.INACTIVE);
                    friendtoAdd.setName(searchResult);
                    friendtoAdd.setId(friendId);
                    friendEmailMap.put(email, friendtoAdd);
                    searchResult = null;    // Clear search result
                    friendAdapter.setFriends(new ArrayList<>(friendEmailMap.values()));
                    dialog.dismiss();
                }, error -> {
                    Log.e(TAG, error.toString());
                    Toast.makeText(main, "Check if the friend is already added.", Toast.LENGTH_SHORT).show();
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
                        headers.put("Authorization", main.getAccount().getIdToken());
                        return headers;
                    }
                };

                queue.add(stringRequest);

            } else {
                Toast.makeText(requireContext(), "User not found. Please try again", Toast.LENGTH_LONG).show();
            }
        });

        newFriendPopup_cancelButton.setOnClickListener(v -> {
            friendAdapter.setFriends(new ArrayList<>(friendEmailMap.values()));
            dialog.dismiss();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResult = null;
                email = searchView.getQuery().toString();

                if (email.equals(main.getAccount().getEmail())) {
                    Toast.makeText(main, "Cannot add yourself as friend!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                final String url = "http://13.89.36.134:8000/friend/" + email;
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            Log.d(TAG, "Successful");
                            try {
                                searchResult = (String) response.get("name");
                                friendId = (String) response.get("friendId");
                                searchResultView.setText(searchResult);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            Log.d(TAG, error.toString());
                            searchResultView.setText("Not found");
                }) {
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

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setFriendsList(JSONObject friend, ArrayList<String> old_friendList) throws JSONException {
        final String email = (String) friend.get("email");
        if (friendEmailMap.containsKey(email)) {
            old_friendList.remove(email);
            FriendModel existFriend = friendEmailMap.get(email);
            assert existFriend != null;
            if ((boolean) friend.get("status")) {
                existFriend.setStatus(FriendStatus.STUDYING);
            } else {
                existFriend.setStatus(FriendStatus.INACTIVE);
            }
        } else {
            FriendModel friendtoAdd = new FriendModel();
            friendtoAdd.setEmail(email);
            if ((boolean) friend.get("status")){
                friendtoAdd.setStatus(FriendStatus.STUDYING);
            } else {
                friendtoAdd.setStatus(FriendStatus.INACTIVE);
            }
            friendtoAdd.setName((String) friend.get("name"));
            friendtoAdd.setId((String) friend.get("friendId"));
            friendEmailMap.put(email, friendtoAdd);
        }
    }

    public void stopFriendListTimer() {
        refreshFriendList.cancel();
    }
}