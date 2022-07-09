package com.projectmonterey.livedetect.object_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectmonterey.R;
import com.projectmonterey.livedetect.classifiers.ObjectDetectionClassifier;


public class LearnMore extends AppCompatActivity {
    private static final String LABELS_FILE = "file:///android_asset/objectlabelmap.txt";
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    TextView obj,def;
    WebView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more);
        Intent intent=getIntent();
        String name=intent.getStringExtra("Name");
        String deff=intent.getStringExtra("DEF");
        //String lk;
        //lk=
        //lk="https://imgur.com/hNXP3t2";
        String link = "https://media.istockphoto.com/photos/grey-reusable-bottle-on-grey-background-picture-id1299291084?b=1&k=20&m=1299291084&s=612x612&w=0&h=hGhYT35eg9QMS7PymtPdWN9_y9GCeks5Nr7MUjFj3D0=";
        obj=findViewById(R.id.OBJ);
        obj.setText(name);
        def=findViewById(R.id.DEF);
        def.setText(deff);
        pic = findViewById(R.id.WEB);
        pic.loadUrl(link);
        //setContentView(pic);
    }
    public void go_back(View view){
        finish();
    }
}