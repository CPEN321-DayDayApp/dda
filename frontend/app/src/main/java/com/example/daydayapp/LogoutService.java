package com.example.daydayapp;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.concurrent.Executor;

public class LogoutService extends Service {
    private final String TAG = "Logout Service";

    @Override
    public void onCreate(){
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // retreive string from intent
        GoogleSignInClient client = intent.getParcelableExtra("GOOGLE");
        //Some code
        CountDownTimer timer = new CountDownTimer(30 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //Some code
                Log.e(TAG, "Service Started");
            }

            public void onFinish() {
                Log.e(TAG, "Call Logout by Service");
                client.signOut()
                        .addOnCompleteListener((Executor) LogoutService.this, task -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
                stopSelf();
            }
        };
        timer.start();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}