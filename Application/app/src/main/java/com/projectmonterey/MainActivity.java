package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void startLearning(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;

        if (user != null) {
            intent = new Intent(this, UiMainActivity.class);
        }
        else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
    }

    public void setting(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
