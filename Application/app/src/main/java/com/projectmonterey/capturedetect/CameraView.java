package com.projectmonterey.capturedetect;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.FaceDetectionListener;

import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    public Camera camera;
    SurfaceHolder surfaceHolder;
    // We need the phone orientation to correctly draw the overlay:
    private int mOrientation;
    private int mOrientationCompensation;
    private OrientationEventListener mOrientationEventListener;

    // Let's keep track of the display rotation and orientation also:
    private int mDisplayRotation;
    private int mDisplayOrientation;

    // Holds the Face Detection result:
    private Camera.Face[] mFaces;

    // The surface view for the camera data
    private SurfaceView mView;

    // Draw rectangles and other fancy stuff:
    /**
     * Sets the faces for the overlay view, so it can be updated
     * and the face overlays will be drawn again.
     */
    /*private FaceDetectionListener faceDetectionListener = new FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            Log.d("onFaceDetection", "Number of Faces:" + faces.length);
            // Update the view now!
            mFaceView.setFaces(faces);
        }
    };*/
    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, Camera camera){
        super(context);
        this.camera = camera;
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
    }

    public CameraView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Camera.Parameters parameters = camera.getParameters();

        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            parameters.set("orientation","landscape");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);
        }
        else{
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);

        }
        camera.setParameters(parameters);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }

    }
    /*public void setCameraPhotoQuality(Camera camera) {
        final Camera.Parameters parameters = camera.getParameters();

        parameters.setPictureFormat(PixelFormat.JPEG);

        if (configurationProvider.getMediaQuality() == Configuration.MEDIA_QUALITY_LOW) {
            parameters.setJpegQuality(50);
        } else if (configurationProvider.getMediaQuality() == Configuration.MEDIA_QUALITY_MEDIUM) {
            parameters.setJpegQuality(75);
        } else if (configurationProvider.getMediaQuality() == Configuration.MEDIA_QUALITY_HIGH) {
            parameters.setJpegQuality(100);
        } else if (configurationProvider.getMediaQuality() == Configuration.MEDIA_QUALITY_HIGHEST) {
            parameters.setJpegQuality(100);
        }
        parameters.setPictureSize(photoSize.getWidth(), photoSize.getHeight());

        camera.setParameters(parameters);
    }*/

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
