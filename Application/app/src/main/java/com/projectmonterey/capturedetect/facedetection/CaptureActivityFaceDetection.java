package com.projectmonterey.capturedetect.facedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
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
import com.projectmonterey.capturedetect.CameraActivity;
import com.projectmonterey.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class CaptureActivityFaceDetection extends AppCompatActivity {
    private FaceDetectorOptions highAccuracyOpts,realTimeOpts;
    private List<Rect> boundingboxes;
    private int scrnnumb = 0;
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
        int flipped = getIntent().getIntExtra("Orientation",1);
        bitmap = transposeBitmap(CameraActivity.captureimg,flipped);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(CaptureActivityFaceDetection.this.getResources(),bitmap);
        view.setBackground(bitmapDrawable);
        if(savePermissions()) {
            saveCaptureToFile();
        }

        // High-accuracy landmark detection and face classification
        highAccuracyOpts = new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize((float) 0.03)
                        .build();

// Real-time contour detection
        realTimeOpts = new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .setMinFaceSize((float) 0.09)
                        .build();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        if(faces.isEmpty())
                                        {
                                            Toast.makeText(getApplicationContext(),"no faces were detected in this image",Toast.LENGTH_SHORT);
                                        }
                                        getFaceInfo(faces);
                                        thefaces = faces;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                int NO_facesDetected = faces.size();
                                                Bitmap getBitmap = drawDetectionResult(boundingboxes);
                                                view.setBackground(new BitmapDrawable(CaptureActivityFaceDetection.this.getResources(),getBitmap));
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
                ActivityCompat.requestPermissions(CaptureActivityFaceDetection.this,
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
                ActivityCompat.requestPermissions(CaptureActivityFaceDetection.this,
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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) findViewById(R.id.capturecanvasview).getLayoutParams();
        ConstraintLayout.LayoutParams newparams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.FILL_PARENT, ConstraintLayout.LayoutParams.FILL_PARENT);
        newparams.leftToLeft=params.leftToLeft;
        newparams.rightToRight=params.rightToRight;
        newparams.topToBottom=params.topToBottom;
        newparams.bottomToTop=params.bottomToTop;
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

    public Bitmap transposeBitmap(Bitmap b,int flipped){
        //Rotate bitmap via empty matrix
        if(flipped==1) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap newbitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            return newbitmap;
        }else{
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
            //Horizontal flip
            matrix.preScale(1.0f, -1.0f);
            Bitmap newbitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            return newbitmap;
        }
    }
    protected Bitmap drawDetectionResult(
            List<Rect> detectionResults
    ) {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint mPaint = new Paint();
        Paint mTextPaint = new Paint();
        mPaint.setTextAlign(Paint.Align.LEFT);

        for (Rect box : detectionResults) {
            // draw bounding box
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setAlpha(128);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(8F);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(box, mPaint);

            Rect tagSize = new Rect(0, 0, 0, 0);

            mTextPaint.setAntiAlias(true);
            mTextPaint.setDither(true);
            mTextPaint.setTextSize(20);
            mTextPaint.setColor(Color.GREEN);
            // calculate the right font size
            mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mTextPaint.setColor(Color.YELLOW);
            mTextPaint.setStrokeWidth(2F);
            String text = "Face";
            mTextPaint.setTextSize(96F);
            mTextPaint.getTextBounds(text, 0, text.length(), tagSize);
            float fontSize = mTextPaint.getTextSize() * box.width() / tagSize.width();

            // adjust the font size so texts are inside the bounding box
            if (fontSize < mTextPaint.getTextSize()) {
                mTextPaint.setTextSize(fontSize);
            }

            float margin = (box.width() - tagSize.width()) / 2.0F;
            if (margin < 0F) margin = 0F;
            canvas.drawText(
                    text, box.left + margin,
                    box.top + tagSize.height(), mTextPaint
            );
        }
        return outputBitmap;
    }
}