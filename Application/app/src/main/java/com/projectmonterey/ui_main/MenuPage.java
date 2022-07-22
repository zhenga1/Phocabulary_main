package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectmonterey.R;
import com.projectmonterey.capturedetect.CameraActivity;
import com.projectmonterey.livedetect.object_detection.CameraActivityYolo;

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
        Intent intent = new Intent(this, CameraActivityYolo.class);
        startActivity(intent);
    }
    public void setting(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void revision(View view) {
<<<<<<< HEAD
        Intent intent = new Intent(this, ans_choosing.class);
=======
        //still not ok
        Intent intent = new Intent(this, RevisionLibrary.class);
>>>>>>> a348078993bafca9cfed4861297f45a9bfa85fc8
        startActivity(intent);
    }
}