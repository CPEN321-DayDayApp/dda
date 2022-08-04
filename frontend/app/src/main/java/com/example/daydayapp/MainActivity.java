package com.example.daydayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.fragments.friends.FriendsFragment;
import com.example.daydayapp.fragments.leaderboard.LeaderboardFragment;
import com.example.daydayapp.fragments.leaderboard.MyScoreTab;
import com.example.daydayapp.fragments.profile.ProfileFragment;
import com.example.daydayapp.fragments.tdl.TdlFragment;
import com.example.daydayapp.fragments.tdl.TdlListFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = "Main";
    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;
    private RequestQueue queue;
    private final int RC_SIGN_IN = 1;

    private final FragmentManager fm = getSupportFragmentManager();

    private TdlFragment tdlFragment;
    private FriendsFragment friendsFragment;
    private ProfileFragment profileFragment;
    private LeaderboardFragment leaderboardFragment;
    private Fragment active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(MainActivity.this);

        // Read client id from manifest metadata
        Bundle metadata = null;
        try {
            metadata = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert metadata != null;
        final String clientId = (String) metadata.get("google_client_id");

        checkLocationPermissions();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(clientId)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
        }
        else
            signIn();
    }

    public void sendRegistrationToServer(String newToken) {
        System.out.println("sendRegistrationToServer starts");
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://13.89.36.134:8000/user/token" ;
        HashMap<String, String> content = new HashMap<>();
        content.put("token", newToken);
        JSONObject jsonContent = new JSONObject(content);
        final String mRequestBody = jsonContent.toString();
        System.out.println(mRequestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> Log.e("registration token is here", newToken),
                error -> Log.e(TAG, error.toString())) {
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
                headers.put("Authorization", account.getIdToken());
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.e("Test", account.getIdToken());

            RequestQueue queue = Volley.newRequestQueue(this);

            final String url = "http://13.89.36.134:8000/user";
            HashMap<String, String> content = new HashMap<>();
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            content.put("token", refreshedToken);
            JSONObject jsonContent = new JSONObject(content);
            final String mRequestBody = jsonContent.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                Log.i("LOG_RESPONSE", response);
                updateUI(account);
            }, error ->
                    Log.e("LOG_RESPONSE", "Error signin" + error.toString()))
            {
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
                    headers.put("Authorization", account.getIdToken());
                    return headers;
                }
            };

            queue.add(stringRequest);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_LONG).show();
            signIn();
        } else {
            this.account = account;

            // TODO: check if user is already set age and gender


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View view = MainActivity.this.getLayoutInflater().inflate(R.layout.number_picker_dialog, null);
            builder.setView(view);
            builder.setTitle("Choose your age: ");
            final NumberPicker picker = view.findViewById(R.id.picker);

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
                        headers.put("Authorization", MainActivity.this.getAccount().getIdToken());
                        return headers;
                    }
                };
                queue.add(stringRequest);

            }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                // Negative button action
            });

            builder.create().show();

            final View genderView = MainActivity.this.getLayoutInflater().inflate(R.layout.number_picker_dialog_2, null);
            AlertDialog.Builder genderBuilder = new AlertDialog.Builder(MainActivity.this);
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
                        headers.put("Authorization", MainActivity.this.getAccount().getIdToken());
                        return headers;
                    }
                };
                queue.add(stringRequest);
            }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                // Negative button action
            });

            genderBuilder.create().show();

            tdlFragment = new TdlFragment(MainActivity.this);
            friendsFragment = new FriendsFragment(MainActivity.this);
            profileFragment = new ProfileFragment(MainActivity.this);
            leaderboardFragment = new LeaderboardFragment(MainActivity.this);

            fm.beginTransaction().add(R.id.flFragment, tdlFragment, "TDL").commit();
            fm.beginTransaction().add(R.id.flFragment, friendsFragment, "FRIENDS").hide(friendsFragment).commit();
            fm.beginTransaction().add(R.id.flFragment, leaderboardFragment, "LB").hide(leaderboardFragment).commit();
            fm.beginTransaction().add(R.id.flFragment, profileFragment, "PROFILE").hide(profileFragment).commit();

            active = tdlFragment;

            NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnItemSelectedListener(this);
            bottomNavigationView.setSelectedItemId(R.id.tdl);
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this, instanceIdResult -> {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);
                sendRegistrationToServer(newToken);
            });

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG, "Call Logout by Service");
                    mGoogleSignInClient.silentSignIn().addOnCompleteListener(task -> {
                            MainActivity.this.account = task.getResult();
                            Log.e(TAG, MainActivity.this.account.getIdToken());
                    });
                }
            }, 0, 300000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tdl:
                fm.beginTransaction().hide(active).show(tdlFragment).commit();
                active = tdlFragment;
                break;

            case R.id.friends:
                fm.beginTransaction().hide(active).show(friendsFragment).commit();
                active = friendsFragment;
                break;

            case R.id.leaderboard:
                fm.beginTransaction().hide(active).show(leaderboardFragment).commit();
                active = leaderboardFragment;
                break;

            case R.id.profile:
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                break;
            default:
                return false;
        }
        return true;
    }

    public GoogleSignInAccount getAccount() {
        return this.account;
    }

    public GoogleSignInClient getGSC() {
        return this.mGoogleSignInClient;
    }

    public MyScoreTab getMyScoreTab() {
        return leaderboardFragment.getMyScoreTab();
    }

    public void stopFriendListTimer() {
        friendsFragment.stopFriendListTimer();
    }

    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new  AlertDialog.Builder(MainActivity.this)
                        .setTitle("Need Location Permissions")
                        .setMessage("We need location permissions to mark your location on a map")
                        .setNegativeButton("CANCEL", (dialog, which) -> {
                            Toast.makeText(MainActivity.this, "We need these location permissions to run!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        })
                        .setPositiveButton("OK", (dialog, which) ->
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
}
