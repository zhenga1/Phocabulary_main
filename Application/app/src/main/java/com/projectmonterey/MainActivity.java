package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth authInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authInstance = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
    }
    public void startLearning(View view) {
        FirebaseUser user = authInstance.getCurrentUser();
        Intent intent;

        if (user != null) {
            // a user is logged in, go to main page
            intent = new Intent(this, UiMainActivity.class);
        }
        else {
            // user is not logged in, go to signup page
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
    }

    public void setting(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void test(View view) {
        Intent intent = new Intent(this, ans_choosing.class);
        startActivity(intent);
    }
    public void shop(View view){
        Intent intent = new Intent(this,ShopActivity.class);
        startActivity(intent);
    }
}
