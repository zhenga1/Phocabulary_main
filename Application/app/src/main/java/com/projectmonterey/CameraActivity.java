package com.projectmonterey;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.security.Permissions;

public class CameraActivity extends AppCompatActivity {
    protected CameraView cameraView;
    protected Camera camera;
    private final int CAMERA_CODE=1000;
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