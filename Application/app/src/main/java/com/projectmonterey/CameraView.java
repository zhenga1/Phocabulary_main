package com.projectmonterey;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
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
        if(this.getResources().getConfiguration().orientation!= Configuration.ORIENTATION_PORTRAIT){
            parameters.set("orientation","portrait");
            camera.setDisplayOrientation(0);
            parameters.setRotation(0);
        }
        else{
            parameters.set("orientation","landscape");
            camera.setDisplayOrientation(90);
            parameters.setRotation(90);

        }
        camera.setParameters(parameters);
        try{
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
