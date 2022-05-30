package com.projectmonterey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public final int CAMERA_CODE=1000,CAPTURE_CODE=2000,STORAGE_CODE=3000;
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
        if(!checkWriteFilePermission())
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
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
        if(!checkCameraPermission()){
            Toast.makeText(getApplicationContext(),"The Camera permissions are not granted!",Toast.LENGTH_LONG).show();
            return;
        }
        if(setupCamera)
        {
            int [] position = new int[2];
            frameLayout.getLocationOnScreen(position);
            Log.e("Error","Camera View position is incorrect" + Integer.toString(position[0]));
            //captureimg = getScreenshot(position[0],position[1],frameLayout.getWidth(),frameLayout.getHeight());
            /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, CAPTURE_CODE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    camera.setDisplayOrientation(90);
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] bytes, Camera camera) {
                            int w = camera.getParameters().getPictureSize().width;
                            int h = camera.getParameters().getPictureSize().height;
                            if(w*h==bytes.length)
                            {
                                System.out.println("Yes");
                            }
                            /*byte[] newbytes = new byte[bytes.length];
                            for(int i=0;i<h;i++){
                                for(int j=0;j<w;j++){
                                    newbytes[i*w+j] = bytes[j*h+i];
                                }
                            }*/

                            captureimg = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                            Intent intent = new Intent(CameraActivity.this,CaptureActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"The camera is not set up", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == CAPTURE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }*/
    }

    public boolean checkCameraPermission()
    {
        String permission = Manifest.permission.CAMERA;
        int res = this.getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    public boolean checkWriteFilePermission()
    {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
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
    public void take(View view){
    }

}