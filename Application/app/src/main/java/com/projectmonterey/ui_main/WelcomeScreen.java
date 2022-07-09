package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.projectmonterey.MainActivity;
import com.projectmonterey.R;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        //getSupportActionBar().hide();
        Intent i = new Intent(WelcomeScreen.this, MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(WelcomeScreen.this, MainActivity.class);
                WelcomeScreen.this.startActivity(i);
                startActivity(i);
                // close this activity
                WelcomeScreen.this.finish();
            }
        }, 1500);
    }
}