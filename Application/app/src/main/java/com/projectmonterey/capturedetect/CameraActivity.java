package com.projectmonterey.capturedetect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//gallery

import com.projectmonterey.R;
import com.projectmonterey.capturedetect.facedetection.CaptureActivityFaceDetection;

public class CameraActivity extends AppCompatActivity {
    protected CameraView cameraView;
    public static Bitmap captureimg;
    public final int FRONT_FACING=0,BACK_FACING=1;
    public int CAMERA_ORIENTATION=BACK_FACING;
    protected Camera camera;
    public float focusAreaSize = 300;
    public final int CAMERA_CODE=1000,CAPTURE_CODE=2000,STORAGE_CODE=3000;
    private boolean setupCamera=false;
    private View.OnTouchListener onTouchListener;
    protected FrameLayout frameLayout;
    private Matrix matrix = new Matrix();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        requestCameraPermissions();
        frameLayout = findViewById(R.id.frameLayout);
        onTouchListener = getOnTouchListener();
        //frameLayout.setOnTouchListener(onTouchListener);

        if(checkCameraPermission()) {
            initCamera(BACK_FACING);
        }

    }
    public void requestCameraPermissions(){
        if(checkCameraHardware(getApplicationContext()) && !checkCameraPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
            }
            else{
                ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
            }
        }
        if(!checkWriteFilePermission())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
            }
            else{
                ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
            }
        }
    }
    private void initCamera(int orientation){
        if(orientation==BACK_FACING){
            camera = Camera.open();
            cameraView = new CameraView(this, camera);
            cameraView.setOnTouchListener(onTouchListener);
            cameraView.setId(R.id.mycameraView);
            frameLayout.addView(cameraView);
            setupCamera=true;
            checkPreviewMatrix();
        }else if(orientation == FRONT_FACING){
            if(getFrontCameraId()!=-1) {
                camera = Camera.open(getFrontCameraId());
                cameraView = new CameraView(this, camera);
                cameraView.setOnTouchListener(onTouchListener);
                cameraView.setId(R.id.mycameraView);
                frameLayout.addView(cameraView);
                setupCamera=true;
                checkPreviewMatrix();
            }
            else{
                Toast.makeText(getApplicationContext(),"NO BACK CAMERA", Toast.LENGTH_SHORT);
                return; //NO BACK CAMERA
            }
        }
        CAMERA_ORIENTATION=orientation;
    }
    public int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) return i;
        }
        return -1; // No front-facing camera found
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(setupCamera){
            checkPreviewMatrix();
            setupCamera=false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(setupCamera) {
            checkPreviewMatrix();
        }

    }

    int handlerTime = 0;
    private void focusCamera(MotionEvent event){
        if (camera != null) {

            camera.cancelAutoFocus();
            Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f);
            Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f);

            Camera.Parameters parameters = camera.getParameters();
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
            final String curFocusMode = parameters.getFocusMode();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            try {
                camera.setParameters(parameters);
            }catch(RuntimeException e){
                e.printStackTrace();
            }
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success || handlerTime>10){
                        Camera.Parameters params = camera.getParameters();
                        if (!params.getFocusMode().equals(curFocusMode)) {
                            params.setFocusMode(curFocusMode);
                            try{
                                camera.setParameters(params);
                            }catch (RuntimeException e)
                            {
                                e.printStackTrace();
                            }
                            handlerTime=0;
                        }
                    }
                    else{
                        handlerTime++;
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
        matrix.mapRect(rectF);

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
    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for
        // documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
    public void checkPreviewMatrix() {
        Matrix matrix = new Matrix();
        if (camera != null) {
            matrix.postRotate(getDisplayOrientation(0,0));
        }
        float viewWidth = frameLayout.getWidth();
        float viewHeight = frameLayout.getHeight();
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
        matrix.invert(this.matrix);
    }

    public void flipCamera(View view){
        camera.stopPreview();
        camera.release();
        camera = null;
        CAMERA_ORIENTATION=CAMERA_ORIENTATION^1;
        initCamera(CAMERA_ORIENTATION);

    }

    protected int getScreenRotation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
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
                    //camera.setDisplayOrientation(90);
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
                            Intent intent = new Intent(CameraActivity.this, CaptureActivityFaceDetection.class);
                            intent.putExtra("Orientation",CAMERA_ORIENTATION);
                            intent.putExtra("Rotation",getScreenRotation());
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
        int res = CameraActivity.this.getApplicationContext().checkCallingOrSelfPermission(permission);
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
            initCamera(CAMERA_ORIENTATION);
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
    //private int TFRequestCodes = 1;
    public void gall(View view){

    }

}