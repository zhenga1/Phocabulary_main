package com.projectmonterey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class CaptureActivity extends AppCompatActivity {
    private FaceDetectorOptions highAccuracyOpts,realTimeOpts;
    private List<Rect> boundingboxes;
    private int scrnnumb = 0;
    private FaceOverlayView faceOverlayView;
    private Bitmap bitmap;
    private ConstraintLayout parent;
    private List<Face> thefaces;
    private float rotY,rotZ,smileProb,rightEyeOpenProb;
    private int WRITE_REQUEST_CODE=10;
    private int READ_REQUEST_CODE=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        parent = findViewById(R.id.parent_constraint_layout);
        addFaceLayout();
        CaptureView view = (CaptureView) findViewById(R.id.capturecanvasview);
        bitmap = transposeBitmap(CameraActivity.captureimg);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(CaptureActivity.this.getResources(),bitmap);
        view.setBackground(bitmapDrawable);
        if(savePermissions()) {
            saveCaptureToFile();
        }

        // High-accuracy landmark detection and face classification
        highAccuracyOpts = new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

// Real-time contour detection
        realTimeOpts = new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        getFaceInfo(faces);
                                        thefaces = faces;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                faceOverlayView.setFaces(thefaces,boundingboxes);
                                                Paint paint = new Paint();
                                                paint.setColor(Color.BLACK);
                                                paint.setStrokeWidth(5.0f);
                                                Bitmap canvasBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                                                Canvas canvas = new Canvas(canvasBitmap);
                                                for(Rect rect : boundingboxes) {
                                                    canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
                                                }
                                                faceOverlayView.invalidate();
                                            }
                                        });

                                        // Task completed successfully
                                        // ...
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"FaceDetectionFailed",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });

    }
    private boolean savePermissions(){
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if(write == PackageManager.PERMISSION_GRANTED && read ==PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if(write != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getApplicationContext(),"The application needs the permission of write storage in order to save the drawn image",Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(CaptureActivity.this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        WRITE_REQUEST_CODE);
            }
        }
        if(read != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    Toast.makeText(getApplicationContext(),"The application needs the permission of write storage in order to save the drawn image",Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(CaptureActivity.this,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        READ_REQUEST_CODE);
            }
        }
        return false;
    }
    private File getreqdcapfile(){
        File file = new File(Environment.getExternalStorageDirectory()+"/Captures/"+"scrnfile"+Integer.toString(scrnnumb)+".jpg");
        while (file.exists()){
            scrnnumb +=1;
            file = new File(Environment.getExternalStorageDirectory()+"/Captures/"+"scrnfile"+Integer.toString(scrnnumb)+".jpg");
        }
        return file;
    }
    private void saveCaptureToFile(){
        File folder = new File(Environment.getExternalStorageDirectory()+"/Captures");
        boolean success = true;
        if(!folder.exists()) success = folder.mkdir();
        if(success){
            File file = getreqdcapfile();
            scrnnumb+=1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            Toast.makeText(getApplicationContext(),"Sucessfully captured and saved image",Toast.LENGTH_SHORT).show();
        }
    }
    private void addFaceLayout(){
        faceOverlayView = new FaceOverlayView(this);
        faceOverlayView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.FILL_PARENT, ConstraintLayout.LayoutParams.FILL_PARENT));
        parent.addView(faceOverlayView);
    }
    public void getFaceInfo(List<Face> faces){
        boundingboxes = new ArrayList<>();
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            boundingboxes.add(bounds);
            rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                PointF leftEarPos = leftEar.getPosition();
            }

            // If contour detection was enabled:
            //List<PointF> leftEyeContour =
                    //face.getContour(FaceContour.LEFT_EYE).getPoints();
            //List<PointF> upperLipBottomContour =
                    //face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();

            // If classification was enabled:
            if (face.getSmilingProbability() != null) {
                smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != null) {
                rightEyeOpenProb = face.getRightEyeOpenProbability();
            }

            // If face tracking was enabled:
            if (face.getTrackingId() != null) {
                int id = face.getTrackingId();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==WRITE_REQUEST_CODE){
            saveCaptureToFile();
        }
    }

    public Bitmap transposeBitmap(Bitmap b){
        //Rotate bitmap via empty matrix
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap newbitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        return newbitmap;
    }
}