package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

public class CaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        View view = findViewById(R.id.capturecanvasview);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(CaptureActivity.this.getResources(),transposeBitmap(CameraActivity.captureimg));
        view.setBackground(bitmapDrawable);
    }
    public Bitmap transposeBitmap(Bitmap b){
        //Rotate bitmap via empty matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap newbitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        return newbitmap;
    }
}