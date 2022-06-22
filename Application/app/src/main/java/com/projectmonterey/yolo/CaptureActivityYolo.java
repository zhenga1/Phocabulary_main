package com.projectmonterey.yolo;

import androidx.appcompat.app.AppCompatActivity;
import com.projectmonterey.R;
import com.projectmonterey.facedetection.CaptureView;

import android.os.Bundle;

public class CaptureActivityYolo extends AppCompatActivity {
    private CaptureView canvasCaptureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_yolo);
        canvasCaptureView = findViewById(R.id.captureyolocanvasview);
    }

}