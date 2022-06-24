package com.projectmonterey.livedetect.classifiers;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.tensorflow.lite.Interpreter;

/**
 * Wrapper for frozen detection models trained using the Tensorflow Object Detection API:
 * - https://github.com/tensorflow/models/tree/master/research/object_detection
 * where you can find the training code.
 * <p>
 * To use pretrained models in the API or convert to TF Lite models, please see docs for details:
 * - https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/detection_model_zoo.md
 * - https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/running_on_mobile_tensorflowlite.md#running-our-model-on-android
 */
public class ObjectDetectionClassifier implements Classifier {
    private Vector<String> labels = new Vector<>();
    private static final float IMG_STD=128.0f, IMG_MEAN=128.0f;
    private Interpreter tflite;
    private int inputSize;
    private boolean isModelQuantised = false;

    //MAX NO OF RESULTS
    private final static int NUM_DETECTIONS = 10;
    private int[] intvalues;

    private ByteBuffer imageData;
    //FLoat array of dimensions
    // BatchSize * Objects((num detected)) * 4 [Dim of rect]
    private float [][][] outputLocations;

    //BatchSize * Objects(num detected))
    //Shows classes of objects that are detected
    private float [][] outputClasses;

    //BatchSize * Objects(num detected)
    //shows the score of each and every detection
    private float [][] outputScore;

    //Array of shape [batchsize], contains exactly the number of detections
    private float [] numDetections;


    public static Classifier create(
            AssetManager assetManager, String modelname,String labelname
            , final int inputSize,
            final boolean isQuantised) throws IOException{
        //Creating new instance of classifier(an object of this class)
        final ObjectDetectionClassifier model  = new ObjectDetectionClassifier();
        InputStream labelsInput;
        String actualfile = labelname.split("file:///android_asset/")[1];
        labelsInput = assetManager.open(actualfile);
        //New reader of the file
        BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine())!=null){
            model.labels.add(line);
        }
        br.close();

        model.inputSize=inputSize;

        try {
            model.tflite = new Interpreter(loadModelFile(assetManager, modelname));
        }catch(Exception e){
            throw new RuntimeException(e);
            //e.printStackTrace();
        }
        model.isModelQuantised = isQuantised;
        int numbytesperchannel;
        if (isQuantised){
            numbytesperchannel = 1; //from (0-255) per channel
        }else{
            numbytesperchannel = 4; //integer / float per channel
        }
        model.imageData = ByteBuffer.allocateDirect(1 * model.inputSize * model.inputSize * 3 * numbytesperchannel);
        model.imageData.order(ByteOrder.nativeOrder());
        model.intvalues = new int[model.inputSize * model.inputSize];

        model.tflite.setNumThreads(2);
        model.outputLocations = new float[1][NUM_DETECTIONS][4];
        model.outputClasses = new float[1][NUM_DETECTIONS];
        model.numDetections = new float[1];
        //Classifier instance
        return model;

    }

    private static MappedByteBuffer loadModelFile(AssetManager assetManager, String filename) throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(filename);
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);

    }
    //THIS IS THE HARDCORE AI PART OF THE CODE, USING TFLITE FOR DETECTIONS
    @Override
    public List<Recognitions> recogniseImage(Bitmap bitmap) {
        bitmap.getPixels(intvalues, 0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        imageData.rewind();

        for( int i = 0;i<inputSize; i++){
            for (int j=0;j<inputSize;j++){
                int pixelVal = intvalues[i*inputSize + j];
                if(isModelQuantised){
                    //& operator: TO MAKE IMAGE DATA VALUE between 0-255
                    //>> operator, to shift integer so that channel data is read
                    imageData.put((byte)(pixelVal>>16 & 0xFF));
                    imageData.put((byte)(pixelVal>>8 & 0xFF));
                    imageData.put((byte)(pixelVal & 0xFF));
                }else{
                    imageData.putFloat(((pixelVal>>16 & 0xFF)-IMG_MEAN)/IMG_STD);
                    imageData.putFloat(((pixelVal>>8 & 0xFF)-IMG_MEAN)/IMG_STD);
                    imageData.putFloat(((pixelVal & 0xFF)-IMG_MEAN)/IMG_STD);
                }
            }
        }
        outputLocations = new float[1][NUM_DETECTIONS][4];
        outputClasses = new float[1][NUM_DETECTIONS];
        outputScore = new float[1][NUM_DETECTIONS];
        numDetections = new float[1];

        Object[] inputArray = {imageData};
        Map<Integer, Object> hashMap = new HashMap<>();

        //Pointer points to the respected lcoations after running for multiple input outputs
        hashMap.put(0,outputLocations);
        hashMap.put(1,outputClasses);
        hashMap.put(2,outputScore);
        hashMap.put(3,numDetections);
        tflite.runForMultipleInputsOutputs(inputArray, hashMap);

        final ArrayList<Recognitions> recognitions = new ArrayList<>(NUM_DETECTIONS);
        for(int i=0;i<NUM_DETECTIONS;i++){
            //USING ssd mobilenet v1 tflite
            //structure outputlocations [top,left,bottom,right]
            final RectF detection =
                    new RectF(
                            outputLocations[0][i][1]*inputSize,
                            outputLocations[0][i][0]*inputSize,
                            outputLocations[0][i][3]*inputSize,
                            outputLocations[0][i][2]*inputSize
                    );
            int labelOff = 1;
            recognitions.add(
                    //Labels are all the effective names of the classes)
                    //Recognitions(title,name,score,confidence(detection),rectangle)
                    new Recognitions(
                            "" + i,
                            labels.get((int) outputClasses[0][i] + labelOff),
                            outputScore[0][i],
                            detection));
        }

        return recognitions;
    }

    @Override
    public void setNumThreads(int numThreads) {
        tflite.setNumThreads(numThreads);
    }
}
