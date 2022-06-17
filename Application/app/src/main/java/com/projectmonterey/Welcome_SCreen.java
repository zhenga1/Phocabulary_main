package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import java.util.concurrent.TimeUnit;

public class Welcome_SCreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        //getSupportActionBar().hide();
        Intent i = new Intent(Welcome_SCreen.this, MainActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
<<<<<<< Updated upstream
<<<<<<< Updated upstream
                Intent i = new Intent(Welcome_SCreen.this, MainActivity.class);
                Welcome_SCreen.this.startActivity(i);
=======
=======
>>>>>>> Stashed changes
                startActivity(i);
>>>>>>> Stashed changes
                // close this activity
                Welcome_SCreen.this.finish();
            }
        }, 3000);
    }
}