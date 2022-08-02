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
import java.util.Collections;
import java.util.Comparator;
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
            Color.parseColor("#AAAAFF")
    };
//    Color.parseColor("#FFFFAA"),
//            Color.parseColor("#55AAAA"),
//            Color.parseColor("#AA33AA"),
//            Color.parseColor("#0D0068"),
//            Color.parseColor("#AF38DD"),
//            Color.parseColor("#BE93D5"),
//            Color.parseColor("#290916"),
//            Color.parseColor("#B65FDF"),
//            Color.parseColor("#00918F"),
//            Color.parseColor("#75E9E5"),
//            Color.parseColor("#AA02B3"),
//            Color.parseColor("#8E1794")
    public final Logger logger = new Logger(ViewOverlay.class);
    private int previewWidth, previewHeight;
    private int sensorOrientation;
    private Matrix frameToCanvasMatrix;
    protected static List<TrackedRecognitions> trackedObjects = new ArrayList<>();

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
        previewWidth = width;
        previewHeight = height;
        this.sensorOrientation = sensorOrientation;
    }
    public synchronized void drawRects(Canvas canvas){
        final boolean rotated = sensorOrientation % 180 == 90;
        float multiplierh=
                        canvas.getHeight() / (float) (rotated ? previewWidth : previewHeight);
        float multiplierw=
                        canvas.getWidth() / (float) (rotated ? previewHeight : previewWidth);
        frameToCanvasMatrix =
                ImageUtils.getTransformationMatrix(
                        previewWidth,
                        previewHeight,
                        (int) (multiplierw * (rotated ? previewHeight : previewWidth)),
                        (int) (multiplierh * (rotated ? previewWidth : previewHeight)),
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
    public static synchronized TrackedRecognitions getRect(float x, float y){
        List<ViewOverlay.TrackedRecognitions> recognitionsListNew = new ArrayList<>();
        //List<RectF> rectBoxes = new ArrayList<>();
        List<Float> floats = new ArrayList<>();
        for(ViewOverlay.TrackedRecognitions recognitions: ViewOverlay.trackedObjects) {
            RectF bounding = recognitions.location;
            if (bounding.contains(x, y)) {
                //rectBoxes.add(bounding);
                recognitionsListNew.add(recognitions);
                floats.add(bounding.width()*bounding.height());
            }

        }
        if(recognitionsListNew.isEmpty() || floats.isEmpty()){
            return null;
        }
        int index = floats.indexOf(Collections.min(floats));
        return recognitionsListNew.get(index);

    }
    public synchronized void processResults(final List<Classifier.Recognitions> results) {
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
        Collections.sort(rectsToTrack, new Comparator<Pair<Float, Classifier.Recognitions>>() {
            @Override
            public int compare(Pair<Float, Classifier.Recognitions> p1, Pair<Float, Classifier.Recognitions> p2) {
                if (p1.first > p2.first) {
                    return -1;
                } else if (p1.first==p2.first) {
                    return 0; // You can change this to make it then look at the
                    //words alphabetical order
                } else {
                    return 1;
                }
            }
        });
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
    protected static class TrackedRecognitions{
        RectF location;
        float detectConfidence;
        String title;
        int color;


    }


}
