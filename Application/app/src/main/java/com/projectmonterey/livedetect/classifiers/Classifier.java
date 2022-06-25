package com.projectmonterey.livedetect.classifiers;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.List;

//This project's custom object detection classifier
public interface Classifier {
    List<Recognitions> recogniseImage(Bitmap bitmap);
    void setNumThreads(int numThreads);
    public class Recognitions{
        //essentially the class of the object that has been recognised
        private final String id;

        //the display name of recognised class;
        private final String title;

        //confidence of detection result, relative to other detections of objects;
        private final Float confidence;

        /*Optimal location within source image for the appearance of the object*/

        private RectF location;

        public Recognitions(final String id , final String title, final Float confidence, final RectF location) {
            this.title = title;
            this.id = id;
            this.confidence = confidence;
            this.location = location;
        }
        public String getId(){ return id;}
        public String getTitle(){ return title;}
        public Float getConfidence(){return confidence;}
        public RectF getLocation(){return location;}

        public void setLocation(RectF location){
            this.location = location;
        }

        @Override
        public String toString(){
            String string = "";
            if(id!=null){
                string = string+="["+id+"]" + "    ";
            }
            if(title!=null){
                string = string+="["+title+']' + "    ";
            }
            if(confidence!= null){
                string = string+="Confidence Level: "+confidence;
            }
            if(location != null){
                string = string += "location: " + location;
            }
            return string;
        }
    }
}
