package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.projectmonterey.MainActivity;
import com.projectmonterey.R;
import com.projectmonterey.capturedetect.CameraActivity;
import com.projectmonterey.livedetect.object_detection.CameraActivityLive;
import com.projectmonterey.settings.SettingsActivity;

public class MenuPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);
    }
    public void game(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
    public void learn(View view) {
        Intent intent = new Intent(this, CameraActivityLive.class);
        startActivity(intent);
    }
    public void setting(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void revision(View view) {
        Intent intent = new Intent(this, RevisionLibrary.class);
        startActivity(intent);
    }
    public void finishactiv(View view){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(this, MainActivity.class));
        }
        MenuPage.this.finish();
    }
}