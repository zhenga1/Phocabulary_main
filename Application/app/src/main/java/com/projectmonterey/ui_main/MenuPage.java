package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.projectmonterey.R;
import com.projectmonterey.capturedetect.CameraActivity;
import com.projectmonterey.livedetect.object_detection.CameraActivityYolo;

public class MenuPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);
        Toast.makeText(getApplicationContext(),"Successful authentication ", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, RevisionLibrary.class);
        startActivity(intent);
    }
    public void finishactiv(View view){finish();}
}