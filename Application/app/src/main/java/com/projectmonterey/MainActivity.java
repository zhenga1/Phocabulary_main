package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.projectmonterey.backend.LoginActivity;
import com.projectmonterey.backend.UiMainActivity;
import com.projectmonterey.capturedetect.CameraActivity;
import com.projectmonterey.livedetect.object_detection.CameraActivityYolo;
import com.projectmonterey.ui_main.MenuPage;
import com.projectmonterey.ui_main.RevisionLibrary;
import com.projectmonterey.ui_main.SettingsActivity;
import com.projectmonterey.ui_main.ShopActivity;
import com.projectmonterey.ui_main.ans_choosing;
import com.projectmonterey.ui_main.recyclerrevise.WordsRevise;

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
//            intent = new Intent(this, UiMainActivity.class);
            intent = new Intent(this, MenuPage.class);
            startActivity(intent);
        }
        else {
            // user is not logged in, go to signup page
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

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
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }
    public void gotocamera(View view){
        //Intent intent = new Intent(this, CameraActivity.class);
        //startActivity(intent);
        Intent intent = new Intent(this, CameraActivityYolo.class);
        startActivity(intent);
    }
    public void gotocamera2(View view){
        //Intent intent = new Intent(this, CameraActivity.class);
        //startActivity(intent);
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
    public void debug(View view){
//        Intent intent = new Intent(this, RevisionLibrary.class);
//        startActivity(intent);
    }
}
