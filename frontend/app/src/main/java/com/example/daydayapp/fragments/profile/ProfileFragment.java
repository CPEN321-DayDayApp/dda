package com.example.daydayapp.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private final MainActivity main;

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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Logout Button
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            main.stopFriendListTimer();
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(main, task -> startActivity(new Intent(main, MainActivity.class)));
        });

        TextView userEmail = view.findViewById(R.id.user_email);
        TextView userName = view.findViewById(R.id.user_name);
        ImageView userImage = view.findViewById(R.id.user_image);

        userEmail.setText(account.getEmail());
        userName.setText(account.getDisplayName());
        Picasso.with(main).load(account.getPhotoUrl()).resize(250, 250).into(userImage);

        return view;
    }
}