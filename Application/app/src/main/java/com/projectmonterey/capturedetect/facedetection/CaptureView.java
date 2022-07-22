package com.projectmonterey.capturedetect.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
/////OLD CLASS CAN BE REMOVED
public class CaptureView extends View {
    private Bitmap canvasBitmap;
    private Paint canvasPaint;
    public CaptureView(Context context) {
        super(context);
    }

    public CaptureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public CaptureView(Context context, Bitmap bitmap, Paint paint ){
        super(context);
        this.canvasBitmap = bitmap;
        this.canvasPaint = paint;
    }
    @Override
    protected void onDraw(Canvas canvas) {
//draw view
        super.onDraw(canvas);
        if(canvasBitmap != null && canvasPaint != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        }
    }
    public void setCanvasBitmap(Bitmap canvasBitmap) {
        this.canvasBitmap = canvasBitmap;
    }
    public void setCanvasPaint(Paint canvasPaint){
        this.canvasPaint = canvasPaint;
    }

}
