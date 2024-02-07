package com.example.capstoneproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;


    private String state;
    private static final String KEY = "com.lifeline.secret";
    private static final String STATE = "com.lifeline.state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel("mynotification", "mynotification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel("GWALIOR", "GWALIOR", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel2);
        }

        SharedPreferences mSharedPreferences = getSharedPreferences(KEY, MODE_PRIVATE);
        state = mSharedPreferences.getString(STATE, null);

        if (state == "signUp") {
            finish();
            startActivity(new Intent(this, sign_up.class));
        } else if (state == "personalInfo") {
            finish();
            startActivity(new Intent(this, personal_info.class));
        } else if (state == "login") {
            finish();
            startActivity(new Intent(this, login_screen.class));
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(STATE, "signUp");
            editor.commit();
            finish();
            startActivity(new Intent(this, sign_up.class));
        }
    }

}