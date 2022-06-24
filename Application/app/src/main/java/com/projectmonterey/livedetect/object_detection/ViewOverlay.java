package com.projectmonterey.livedetect.object_detection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.projectmonterey.capturedetect.Utils.ImageUtils;
import com.projectmonterey.livedetect.classifiers.Classifier;
import com.projectmonterey.livedetect.env.CustomBorderText;
import com.projectmonterey.livedetect.env.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ViewOverlay extends View {
    private final float textSizePx;
    private static final float DIP_OF_TEXT = 20;
    private List<DrawCallback> callbacks = new ArrayList<>();
    private static final float MIN_SIZE = 16.0f;
    private CustomBorderText customBorderText;
    private Paint boxPaint = new Paint();
    public List<Classifier.Recognitions> trackedRecs;
    private static final int[] COLORS = {
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.WHITE,
            Color.parseColor("#55FF55"),
            Color.parseColor("#FFA500"),
            Color.parseColor("#FF8888"),
            Color.parseColor("#AAAAFF"),
            Color.parseColor("#FFFFAA"),
            Color.parseColor("#55AAAA"),
            Color.parseColor("#AA33AA"),
            Color.parseColor("#0D0068")
    };
    public final Logger logger = new Logger(ViewOverlay.class);
    private int frameWidth,frameHeight;
    private int sensorOrientation;
    private Matrix frameToCanvasMatrix;
    private List<TrackedRecognitions> trackedObjects = new ArrayList<>();

    public ViewOverlay(Context context) {
        super(context);
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(10.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);
        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, DIP_OF_TEXT, context.getResources().getDisplayMetrics());
        customBorderText = new CustomBorderText(textSizePx);

    }

    public ViewOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(10.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);
        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, DIP_OF_TEXT, context.getResources().getDisplayMetrics());
        customBorderText = new CustomBorderText(textSizePx);
    }

    public ViewOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(10.0f);
        boxPaint.setStrokeCap(Paint.Cap.ROUND);
        boxPaint.setStrokeJoin(Paint.Join.ROUND);
        boxPaint.setStrokeMiter(100);
        textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, DIP_OF_TEXT, context.getResources().getDisplayMetrics());
        customBorderText = new CustomBorderText(textSizePx);
    }
    public void addCallback(final DrawCallback callback) {
        callbacks.add(callback);
    }
    @Override
    public synchronized void draw(Canvas canvas) {
        super.draw(canvas);
        for(DrawCallback callback : callbacks){
            callback.drawCanvas(canvas);
        }
    }
    public interface DrawCallback{
        public void drawCanvas(Canvas canvas);
    }
    public synchronized void setFrameConfiguration(final int width, final int height, final int sensorOrientation){
        frameWidth = width;
        frameHeight = height;
        this.sensorOrientation = sensorOrientation;
    }
    public void drawRects(Canvas canvas){
        final boolean rotated = sensorOrientation % 180 == 90;
        final float multiplier =
                Math.min(
                        canvas.getHeight() / (float) (rotated ? frameWidth : frameHeight),
                        canvas.getWidth() / (float) (rotated ? frameHeight : frameWidth));
        frameToCanvasMatrix =
                ImageUtils.getTransformationMatrix(
                        frameWidth,
                        frameHeight,
                        (int) (multiplier * (rotated ? frameHeight : frameWidth)),
                        (int) (multiplier * (rotated ? frameWidth : frameHeight)),
                        sensorOrientation,
                        false);
        if(trackedObjects.isEmpty()){

            logger.e("There are no tracked objects");
            return;
        }
        for (final TrackedRecognitions recognition : trackedObjects) {
            final RectF trackedPos = new RectF(recognition.location);

            frameToCanvasMatrix.mapRect(trackedPos);
            boxPaint.setColor(recognition.color);

            float cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f;
            canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint);

            final String labelString =
                    !TextUtils.isEmpty(recognition.title)
                            ? String.format("%s %.2f", recognition.title, (100 * recognition.detectConfidence))
                            : String.format("%.2f", (100 * recognition.detectConfidence));
            //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
            // labelString);
            customBorderText.drawText(canvas, trackedPos.left+cornerSize, trackedPos.top,labelString,boxPaint);
            //canvas.drawText(labelString, trackedPos.left+cornerSize, trackedPos.top,boxPaint);
        }
    }
    public void processResults(final List<Classifier.Recognitions> results) {
        final List<Pair<Float, Classifier.Recognitions>> rectsToTrack = new LinkedList<>();

        final Matrix rgbFrameToScreen = new Matrix(frameToCanvasMatrix);

        for (final Classifier.Recognitions result : results) {
            if (result.getLocation() == null) {
                continue;
            }
            final RectF detectionFrameRect = new RectF(result.getLocation());

            final RectF detectionScreenRect = new RectF();
            rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect);

            //logger.v("Result! Frame: " + result.getLocation() + " mapped to screen:" + detectionScreenRect);


            if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
                //logger.w("Degenerate rectangle! " + detectionFrameRect);
                continue;
            }

            rectsToTrack.add(new Pair<>(result.getConfidence(), result));
        }

        if (rectsToTrack.isEmpty()) {
            //logger.v("Nothing to track, aborting.");
            return;
        }

        trackedObjects.clear();
        for (final Pair<Float, Classifier.Recognitions> potential : rectsToTrack) {
            final TrackedRecognitions trackedRecognition = new TrackedRecognitions();
            trackedRecognition.detectConfidence = potential.first;
            trackedRecognition.location = new RectF(potential.second.getLocation());
            trackedRecognition.title = potential.second.getTitle();
            trackedRecognition.color = COLORS[trackedObjects.size()];
            trackedObjects.add(trackedRecognition);

            if (trackedObjects.size() >= COLORS.length) {
                break;
            }
        }
    }
    private static class TrackedRecognitions{
        RectF location;
        float detectConfidence;
        String title;
        int color;


    }


}
