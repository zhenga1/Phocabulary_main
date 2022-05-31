package com.projectmonterey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.List;

public class CaptureActivity extends AppCompatActivity {
    private FaceDetectorOptions highAccuracyOpts,realTimeOpts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        View view = findViewById(R.id.capturecanvasview);
        Bitmap bitmap = transposeBitmap(CameraActivity.captureimg);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(CaptureActivity.this.getResources(),bitmap);
        view.setBackground(bitmapDrawable);
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
    public void getFaceInfo(List<Face> faces){
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                PointF leftEarPos = leftEar.getPosition();
            }

            // If contour detection was enabled:
            List<PointF> leftEyeContour =
                    face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> upperLipBottomContour =
                    face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();

            // If classification was enabled:
            if (face.getSmilingProbability() != null) {
                float smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != null) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }

            // If face tracking was enabled:
            if (face.getTrackingId() != null) {
                int id = face.getTrackingId();
            }
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