package com.example.capstoneproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class phone_verification extends AppCompatActivity {

    private static final String KEY = "com.example.argumantedreality.secret";
    private static final String STATE = "com.example.argumantedreality.state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        SharedPreferences.Editor editor = getSharedPreferences(KEY, MODE_PRIVATE).edit();
        editor.putString(STATE, "verification");
        editor.commit();
    }

    public void goToDashboard(View view) {
        finish();
        startActivity(new Intent(this, dashboard.class));
    }
}