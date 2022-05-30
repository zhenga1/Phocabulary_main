package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

public class CaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        View view = findViewById(R.id.capturecanvasview);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(CaptureActivity.this.getResources(),CameraActivity.captureimg);
        view.setBackground(bitmapDrawable);
    }
}