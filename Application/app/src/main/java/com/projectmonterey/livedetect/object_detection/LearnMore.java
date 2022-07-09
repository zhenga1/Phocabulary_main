package com.projectmonterey.livedetect.object_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectmonterey.R;
import com.projectmonterey.livedetect.classifiers.ObjectDetectionClassifier;


public class LearnMore extends AppCompatActivity {
    private static final String LABELS_FILE = "file:///android_asset/objectlabelmap.txt";
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    TextView obj,def;
    ImageView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more);
        Intent intent=getIntent();
        String name=intent.getStringExtra("Name");
        String deff=intent.getStringExtra("DEF");
        obj=findViewById(R.id.OBJ);
        obj.setText(name);
        def=findViewById(R.id.DEF);
        def.setText(deff);


    }
    public void go_back(View view){
        finish();
    }
}