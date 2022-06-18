
package com.projectmonterey;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Camera;
import com.google.mlkit.vision.face.Face;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * This class is a simple View to display the faces.
 */
public class FaceOverlayView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private Bitmap canvasBitmap;
    private int mDisplayOrientation;
    private int mOrientation;
    private List<Rect> mRects;
    private List<Face> mFaces;
    private int width,height;
    private Paint canvasPaint;

    public FaceOverlayView(Context context) {
        super(context);
        initialize();
    }
    private void initialize() {
        // We want a green box around the face:
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);
    }

    public void setFaces(List<Face> faces, List<Rect> rects) {
        mFaces = faces;
        mRects = rects;
        invalidate();
    }
    public void setFaces(List<Rect> rects) {
        mRects = rects;
        invalidate();
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDisplayOrientation(int displayOrientation) {
        mDisplayOrientation = displayOrientation;
        invalidate();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w;
        height = h;
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        boolean drawn = false;
        canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
        if (mFaces != null && mFaces.size() > 0) {
            Matrix matrix = new Matrix();
            Util.prepareMatrix(matrix, false, mDisplayOrientation, getWidth(), getHeight());
            canvas.save();
            matrix.postRotate(mOrientation);
            canvas.rotate(-mOrientation);
            for (int j=0;j<mFaces.size();j++) {
                Rect rect = mFaces.get(j).getBoundingBox();
                RectF fface = new RectF(rect);
                matrix.mapRect(fface);
                canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, mPaint);
                canvas.drawText("Score ", fface.left, fface.top, mTextPaint);
            }
            canvas.restore();
            drawn = true;
        }
        if (drawn == false && mRects != null && mRects.size() > 0) {
            Matrix matrix = new Matrix();
            Util.prepareMatrix(matrix, false, mDisplayOrientation, getWidth(), getHeight());
            canvas.save();
            matrix.postRotate(mOrientation);
            canvas.rotate(-mOrientation);
            for(int j=0;j<mRects.size();j++){
                Rect face = mRects.get(j);
                RectF fface = new RectF(face);
                matrix.mapRect(fface);
                canvas.drawRect(face.left, face.top, face.right, face.bottom,mPaint);
                canvas.drawText("Score ", fface.left, fface.top, mTextPaint);
            }
        }
    }
}