package com.projectmonterey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.security.Permissions;

public class CameraActivity extends AppCompatActivity {
    protected CameraView cameraView;
    public static Bitmap captureimg;
    protected Camera camera;
    private final int CAMERA_CODE=1000,CAPTURE_CODE=2000;
    private boolean setupCamera=false;
    protected FrameLayout frameLayout;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        if(checkCameraHardware(getApplicationContext()) && !checkCameraPermission()){
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
        }
        frameLayout = findViewById(R.id.frameLayout);
        if(checkCameraPermission()) {
            initCamera();
        }

    }
    private void initCamera(){
        camera = Camera.open();
        cameraView = new CameraView(this, camera);
        frameLayout.addView(cameraView);
        setupCamera=true;
    }
    private Bitmap getScreenshot(int x,int y,int width,int height){
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }
    public void capture(View view){
        if(setupCamera)
        {
            int [] position = new int[2];
            cameraView.getLocationOnScreen(position);
            Log.e("Error","Camera View position is incorrect" + Integer.toString(position[0]))
            captureimg = getScreenshot(position[0],position[1],view.getWidth(),view.getHeight());
            Intent intent = new Intent(CameraActivity.this,CaptureActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(getApplicationContext(),"The camera is not set up", Toast.LENGTH_LONG).show();
        }
    }
    public boolean checkCameraPermission()
    {
        String permission = Manifest.permission.CAMERA;
        int res = this.getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_CODE)
        {
            initCamera();
        }
    }

    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


}