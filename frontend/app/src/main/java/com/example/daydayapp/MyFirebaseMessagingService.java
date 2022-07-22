package com.example.daydayapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Refreshed token:", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String body) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() ->
                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_LONG).show());
    }

    private void handleDataMessage(JSONObject json) {
        Log.d(TAG, "Message: " + json.toString());
    }
}