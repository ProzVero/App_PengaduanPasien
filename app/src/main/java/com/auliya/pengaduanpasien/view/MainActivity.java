package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.auliya.pengaduanpasien.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences session_data;
    private boolean isLoggedIn;
    private TextView txt_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    public void init() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                session_data = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                isLoggedIn = session_data.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

}