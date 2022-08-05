package com.example.daydayapp.fragments.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private final String TAG = "profile";
    private final MainActivity main;
    private RequestQueue queue;

    public ProfileFragment(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GoogleSignInAccount account = main.getAccount();
        GoogleSignInClient mGoogleSignInClient = main.getGSC();

        queue = Volley.newRequestQueue(requireActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userEmail = view.findViewById(R.id.user_email);
        TextView userName = view.findViewById(R.id.user_name);
        ImageView userImage = view.findViewById(R.id.user_image);

        userEmail.setText(account.getEmail());
        userName.setText(account.getDisplayName());
        Picasso.with(main).load(account.getPhotoUrl()).resize(250, 250).into(userImage);

        // Logout Button
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            main.stopFriendListTimer();
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(main, task -> startActivity(new Intent(main, MainActivity.class)));
        });

        // Update age and gender Button
        Button updateButton = view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            // TODO: Same as main activity
            AlertDialog.Builder builder = new AlertDialog.Builder(main);
            final View ageView = main.getLayoutInflater().inflate(R.layout.number_picker_dialog, null);
            builder.setView(ageView);
            builder.setTitle("Choose your age: ");
            final NumberPicker picker = ageView.findViewById(R.id.picker);

            picker.setMinValue(0);
            picker.setMaxValue(100);
            picker.setValue(25);

            builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                // Positive button action
                final String url = "http://13.89.36.134:8000/user/age";
                HashMap<String, Integer> content = new HashMap<>();
                content.put("age", picker.getValue());
                JSONObject jsonContent = new JSONObject(content);
                final String mRequestBody = jsonContent.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, response -> {
                    Log.i(TAG, response);
                }, error -> Log.e(TAG, error.toString())) {
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

            }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                // Negative button action
            });

            builder.create().show();

            final View genderView = main.getLayoutInflater().inflate(R.layout.number_picker_dialog_2, null);
            AlertDialog.Builder genderBuilder = new AlertDialog.Builder(main);
            genderBuilder.setView(genderView);
            genderBuilder.setTitle("Choose your gender: ");
            final NumberPicker genderPicker = genderView.findViewById(R.id.picker2);
            final String[] gender = {" ", "male", "female", "other"};

            NumberPicker.Formatter formatter = value -> gender[value];

            genderPicker.setMinValue(0);
            genderPicker.setMaxValue(3);
            genderPicker.setValue(0);
            genderPicker.setFormatter(formatter);

            genderBuilder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                // Positive button action
                if (gender[genderPicker.getValue()].equals("other") || gender[genderPicker.getValue()].equals(" "))
                    return;
                final String url = "http://13.89.36.134:8000/user/gender";
                HashMap<String, String> content = new HashMap<>();
                content.put("gender", gender[genderPicker.getValue()]);
                JSONObject jsonContent = new JSONObject(content);
                final String mRequestBody = jsonContent.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, response -> {
                    Log.i(TAG, response);
                }, error -> Log.e(TAG, error.toString())) {
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
            }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                // Negative button action
            });

            genderBuilder.create().show();
        });

        // Do not ask age and gender Button
        Button doNotAskButton = view.findViewById(R.id.do_not_ask_button);
        doNotAskButton.setOnClickListener(v -> {
            // Check if user is already set age and gender
            final String url = "http://13.89.36.134:8000/user/flag";
            HashMap<String, String> content = new HashMap<>();
            JSONObject jsonContent = new JSONObject(content);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                    response -> {
                        Log.d(TAG, "Successful");
                        try {
                            boolean flag = (boolean) response.get("flag");
                            if (!flag) {
                                final String ageUrl = "http://13.89.36.134:8000/user/age";
                                HashMap<String, Integer> ageContent = new HashMap<>();
                                ageContent.put("age", 25);
                                JSONObject jsonAgeContent = new JSONObject(ageContent);
                                final String mRequestBody = jsonAgeContent.toString();
                                StringRequest stringRequest = new StringRequest(Request.Method.PUT, ageUrl, res -> {
                                    Log.i(TAG, res);
                                }, error -> Log.e(TAG, error.toString())) {
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
        });

        return view;
    }
}