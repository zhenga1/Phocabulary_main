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
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    protected CameraView cameraView;
    public static Bitmap captureimg;
    protected Camera camera;
    public float focusAreaSize = 100;
    public final int CAMERA_CODE=1000,CAPTURE_CODE=2000,STORAGE_CODE=3000;
    private boolean setupCamera=false;
    private View.OnTouchListener onTouchListener;
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
        onTouchListener = getOnTouchListener();
        //frameLayout.setOnTouchListener(onTouchListener);

        if(checkCameraPermission()) {
            initCamera();
        }

    }
    private void initCamera(){
        camera = Camera.open();
        cameraView = new CameraView(this, camera);
        //cameraView.setOnTouchListener(onTouchListener);
        cameraView.setId(R.id.mycameraView);
        frameLayout.addView(cameraView);
        setupCamera=true;
    }
    private void focusCamera(MotionEvent event){
        if (camera != null) {

            camera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);

            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            if(parameters.getMaxNumFocusAreas()>0) {
                List<Camera.Area> myAreaList = new ArrayList<Camera.Area>();
                myAreaList.add(new Camera.Area(focusRect, 800));
                parameters.setFocusAreas(myAreaList);
                /*List<Camera.Area> focusAreas= parameters.getFocusAreas();
                focusAreas.clear();
                focusAreas.add(new Camera.Area(focusRect, 800));
                parameters.setFocusAreas(focusAreas);*/
            }
            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> myAreaList = new ArrayList<Camera.Area>();
                myAreaList.add(new Camera.Area(meteringRect, 800));
                parameters.setFocusAreas(myAreaList);
                /*List<Camera.Area> meteringArea = parameters.getMeteringAreas();
                meteringArea.clear();
                meteringArea.add(new Camera.Area(meteringRect, 800));
                parameters.setMeteringAreas(meteringArea);*/
            }

            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if(!success) {
                        camera.cancelAutoFocus();
                    }
                    Camera.Parameters params = camera.getParameters();
                    if (!params.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Error: The camera is not activated",Toast.LENGTH_LONG).show();
        }
    }
    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int left = clamp((int) x - areaSize / 2, 0, cameraView.getWidth() - areaSize);
        int top = clamp((int) y - areaSize / 2, 0, cameraView.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        //matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
    private View.OnTouchListener getOnTouchListener(){
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float onTouchX = motionEvent.getX();
                float onTouchY = motionEvent.getY();
                if(view.getId()==frameLayout.getId() || view.getId()==cameraView.getId()) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            focusCamera(motionEvent);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                }
                return true;
            }
        };
        return onTouchListener;
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