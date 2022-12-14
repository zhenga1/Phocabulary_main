package com.projectmonterey.livedetect.object_detection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.projectmonterey.R;
import com.projectmonterey.capturedetect.CameraView;
import com.projectmonterey.capturedetect.Utils.ImageUtils;
import com.projectmonterey.livedetect.classifiers.Classifier;
import com.projectmonterey.livedetect.classifiers.ObjectDetectionClassifierSSD;
import com.projectmonterey.livedetect.env.Logger;

public class NoOverlayDetectSSD extends AppCompatActivity implements Camera.PreviewCallback {
    protected CameraView cameraView;
    public final int FRONT_FACING=0,BACK_FACING=1;
    private boolean switchtouchstatus = true;
    private TextView status;
    private Logger logger = new Logger(NoOverlayDetectSSD.class);
    public int CAMERA_ORIENTATION=BACK_FACING;
    public final int SSD_TYPE_DETECTION =0, YOLO_TYPE_DETECTION=1;
    private static final String MODEL_FILE = "objectdetect.tflite";
    private static final String LABELS_FILE = "file:///android_asset/objectlabelmap.txt";
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    private static final String PHOTO_LINK = "file:///android_asset/samplephotolink.txt";
    //300 by 300 image essentially
    private static final int MODEL_INPUT_SIZE = 300;
    public int previewHeight,previewWidth;
    public CustomGif customGif;
    protected Camera camera;
    private Runnable imageConverter, postInferenceCallback;
    public float focusAreaSize = 300;
    public final int CAMERA_CODE=1000,STORAGE_CODE=3000;
    private ViewOverlay trackingOverlay;
    public long timestamp = 0;
    private Bitmap rgbFrameBitmap, croppedBitmap;
    private boolean setupCamera=false;
    private View.OnTouchListener onTouchListener,nullListener;
    private boolean isProcessingFrame = false;
    private static final boolean MAINTAIN_ASPECT = false;
    protected FrameLayout frameLayout;
    private Matrix matrix = new Matrix();
    private int[] rgbBytes;
    private boolean computingImage = false;
    private Classifier detector;
    private Matrix cropToFrameTransform,frameToCropTransform;
    private HandlerThread handlerThread;
    private Handler handler;
    public final static float MINIMUM_CONFIDENCE = 0.5f;
    private TextView tv_debug;
    private Button learning;

    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_ssd);
        trackingOverlay = findViewById(R.id.viewOverlay);
        tv_debug = findViewById(R.id.tv_debug);
        status = findViewById(R.id.statusCamera);
        learning = findViewById(R.id.button12);
        learning.setText(learning.getText()+ "  mode: SSD");
        requestCameraPermissions();
        frameLayout = findViewById(R.id.camerayolo);
        customGif = findViewById(R.id.backgroundimageview);
        onTouchListener = getOnTouchListener();
        nullListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        };
        //frameLayout.setOnTouchListener(onTouchListener);

        if(checkCameraPermission()) {
            initCamera(BACK_FACING);
        }

    }
    public void switchMode(View view){
        Intent intent = new Intent(NoOverlayDetectSSD.this,CameraActivityLiveYOLO.class);
        startActivity(intent);
        NoOverlayDetectSSD.this.finish();
    }
    public void requestCameraPermissions(){
        if(checkCameraHardware(getApplicationContext()) && !checkCameraPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
            }
            else{
                ActivityCompat.requestPermissions(NoOverlayDetectSSD.this,new String[]{Manifest.permission.CAMERA},CAMERA_CODE);
            }
        }
        if(!checkWriteFilePermission())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
            }
            else{
                ActivityCompat.requestPermissions(NoOverlayDetectSSD.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_CODE);
            }
        }
    }
    private void initCamera(int orientation){
        if(orientation==BACK_FACING){
            camera = Camera.open();
            cameraView = new CameraView(this, camera,this);
            cameraView.setOnTouchListener(onTouchListener);
            cameraView.setId(R.id.mycameraView);
            frameLayout.addView(cameraView);
            setupCamera=true;
            checkPreviewMatrix();
        }else if(orientation == FRONT_FACING){
            if(getFrontCameraId()!=-1) {
                camera = Camera.open(getFrontCameraId());
                cameraView = new CameraView(this, camera,this);
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
    protected synchronized void onDestroy() {
        super.onDestroy();
        if(setupCamera){
            checkPreviewMatrix();
            setupCamera=false;
        }
        camera.release();
    }

    @Override
    protected synchronized void onStart() {
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
                if(view.getId()==trackingOverlay.getId()){
                    switch(motionEvent.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_UP:
                            ViewOverlay.TrackedRecognitions recognition = ViewOverlay.getRect(onTouchX,onTouchY);
                            if(recognition!=null){
                                String definition = ObjectDetectionClassifierSSD.labeldefitions.get(ObjectDetectionClassifierSSD.labels.indexOf(recognition.title));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog alertDialog = new AlertDialog.Builder(NoOverlayDetectSSD.this).create();
                                        alertDialog.setTitle(recognition.title);
                                        alertDialog.setMessage(definition);
                                        alertDialog.setButton("Got it", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                customGif.setUrlGif("file:///android_asset/horray.gif");
                                                customGif.configureView();
                                                //Destroy Custom Gif after 0.7 seconds
                                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        customGif.setVisibility(View.GONE);
                                                        customGif.destroyDrawingCache();
                                                    }
                                                },700);
                                            }
                                        });
                                        alertDialog.setButton2("Learn More", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent intent = new Intent(NoOverlayDetectSSD.this, LearnMore.class);
                                                String na=recognition.title;
                                                intent.putExtra("Name", na);
                                                intent.putExtra("DEF", definition);
                                                //intent.putExtra("PHO", "string of the link");
                                                startActivity(intent);
                                            }
                                        });
                                        alertDialog.show();

                                    }
                                });
                            }
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
    public void flipCameraYolo(View view){
        //if(CAMERA_ORIENTATION==BACK_FACING) CAMERA_ORIENTATION = FRONT_FACING;
        //else CAMERA_ORIENTATION = BACK_FACING;
        //initCamera(CAMERA_ORIENTATION);
        camera.stopPreview();
        camera.release();
        camera = null;
        CAMERA_ORIENTATION=CAMERA_ORIENTATION^1;
        initCamera(CAMERA_ORIENTATION);
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
        int res = NoOverlayDetectSSD.this.getApplicationContext().checkCallingOrSelfPermission(permission);
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

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if(isProcessingFrame){
            logger.w("Frame is still droppping!");
            return;
        }
        isProcessingFrame = true;
        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), getScreenRotation());
                }
            }

        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        imageConverter = new Runnable() {
            @Override
            public void run() {
                if(getScreenRotation()!=0) {
                    ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                }else{
                    ImageUtils.convertYUV420SPToARGB8888(bytes, previewHeight, previewWidth, rgbBytes);
                }
            }
        };
        postInferenceCallback = new Runnable() {
            @Override
            public void run() {
                camera.addCallbackBuffer(bytes);
                isProcessingFrame = false;
            }
        };
        imgProcessing();

    }

    //Must be called by button
    public void switchCameraonTouch(View view){
        if(switchtouchstatus){
            status.setText(getResources().getString(R.string.camera_learn));
            cameraView.setOnTouchListener(nullListener);
            trackingOverlay.setOnTouchListener(onTouchListener);
        }else{
            status.setText(getResources().getString(R.string.camera_focus));
            cameraView.setOnTouchListener(onTouchListener);
            trackingOverlay.setOnTouchListener(nullListener);
        }
        switchtouchstatus = ! switchtouchstatus;
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
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    //Choose Preview size, code ran whenever camera is initialised and preview callback is run
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onPreviewSizeChosen(final Size size, final int rotation){
        try {
            detector =
                    ObjectDetectionClassifierSSD.create(
                            getAssets(),
                            MODEL_FILE,
                            LABELS_FILE,
                            DEFINITION_FILE,
                            MODEL_INPUT_SIZE,
                            true
                    );
        } catch (final IOException e) {
            logger.e("Module could not be initialized");
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        int sensorOrientation = rotation - getScreenRotation();

        logger.i(String.format("Current camera orientation is", sensorOrientation));

        logger.i(String.format("Camera Preview width: %d, %d", previewWidth, previewHeight));


        if(getScreenRotation()!=0){
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
            croppedBitmap = Bitmap.createBitmap(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, Bitmap.Config.ARGB_8888);
            frameToCropTransform =
                    ImageUtils.getTransformationMatrix(
                            previewWidth, previewHeight,
                            MODEL_INPUT_SIZE, MODEL_INPUT_SIZE,
                            sensorOrientation,
                            MAINTAIN_ASPECT
                    );
        }else{
            rgbFrameBitmap = Bitmap.createBitmap(previewHeight, previewWidth, Bitmap.Config.ARGB_8888);
            croppedBitmap = Bitmap.createBitmap(MODEL_INPUT_SIZE, MODEL_INPUT_SIZE, Bitmap.Config.ARGB_8888);
            frameToCropTransform =
                    ImageUtils.getTransformationMatrix(
                            previewHeight, previewWidth,
                            MODEL_INPUT_SIZE, MODEL_INPUT_SIZE,
                            sensorOrientation,
                            MAINTAIN_ASPECT
                    );
        }
        cropToFrameTransform = new Matrix();
        //inverse the matrix (idk what for actually haha)
        frameToCropTransform.invert(cropToFrameTransform);
        //IMPORTANT LINE
        trackingOverlay = findViewById(R.id.viewOverlay);
        //trackingOverlay.setVisibility(View.VISIBLE);
        trackingOverlay.addCallback(
                canvas -> {
                    trackingOverlay.drawRects(canvas);
//                    if (BuildConfig.DEBUG) {
//                        tracker.drawDebug(canvas);
//                    }
                });
        if(getScreenRotation()!=0) {
            trackingOverlay.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
        }else{
            trackingOverlay.setFrameConfiguration(previewHeight, previewWidth, sensorOrientation);
        }
    }
    @Override
    public synchronized void onResume(){
        super.onResume();
        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        computingImage = false;
        handler = new Handler(handlerThread.getLooper());
    }
    @Override
    public synchronized void onPause(){
        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            logger.e(e.toString());
        }
        super.onPause();
    }
    private void readyForNextImg(){
        if(postInferenceCallback!=null){
            postInferenceCallback.run();
        }
    }
    private void runInBackground(Runnable runnable){
        if(handler!=null){
            handler.post(runnable);
        }
    }
    //AI PROCESSING IMAGE METHOD
    protected void imgProcessing(){
        ++timestamp;
        trackingOverlay.postInvalidate();

        if(computingImage){
            readyForNextImg();
            return;
        }

        logger.i(String.format("Preparing for image:%d, for processing using ai framework",timestamp));

        computingImage = true;
        logger.i("Preparing image " + timestamp + " for module in bg thread.");
        if(getScreenRotation()!=0) {
            rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        }else{
            rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewHeight, 0, 0, previewHeight, previewWidth);
        }
        readyForNextImg();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

        if(CAMERA_ORIENTATION==FRONT_FACING)
        {
            //Flip the matrix across the horizontal axis
            Matrix flipMatrix = new Matrix();
            flipMatrix.preScale(-1.0f, 1.0f);
            croppedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), flipMatrix, true);
        }
        if(getScreenRotation() == 0)
        {
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(90);
            croppedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), rotateMatrix, true);
        }
        runInBackground(
                () -> {
                    final long startTime = SystemClock.uptimeMillis();
                    // ????????????
                    // importantthing
                    final List<Classifier.Recognitions> results = detector.recogniseImage(
                            hasFrontCamera()? croppedBitmap : flip(croppedBitmap));
                    long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                    logger.i("Running detection on image " + lastProcessingTimeMs);

                    final List<Classifier.Recognitions> mappedRecognitions = new LinkedList<>();

                    for (final Classifier.Recognitions result : results) {
                        final RectF location = result.getLocation();
                        if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE) {

                            cropToFrameTransform.mapRect(location);

                            result.setLocation(location);
                            mappedRecognitions.add(result);
                        }
                    }

                    trackingOverlay.processResults(mappedRecognitions);
                    trackingOverlay.postInvalidate();
                    computingImage = false;

                    runOnUiThread(
                            () -> {
                                tv_debug.setText(lastProcessingTimeMs +"ms");
                            });
                });


    }
    private boolean hasFrontCamera() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_BACK) return true;
        }

        return false;
    }
    private Bitmap flip(Bitmap d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(d, 0, 0, d.getWidth(), d.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }
    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    //private int TFRequestCodes = 1;

}