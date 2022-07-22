package com.projectmonterey.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectmonterey.MainActivity;
import com.projectmonterey.R;
import com.projectmonterey.livedetect.object_detection.CameraActivityYolo;
import com.projectmonterey.ui_main.WelcomeScreen;

public class set_General extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_general);
    }
    public void Logout(View view){
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}