package com.projectmonterey.livedetect.object_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.projectmonterey.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


public class LearnMore extends AppCompatActivity {
    private static final String LABELS_FILE = "file:///android_asset/objectlabelmap.txt";
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    public static Vector<String> labels = new Vector<>();
    public static Vector<String> links = new Vector<>();
    TextView obj,def;
    WebView pic;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_more);
        Intent intent=getIntent();
        String name=intent.getStringExtra("Name");
        String deff=intent.getStringExtra("DEF");
        String link;
        link= "https://media.istockphoto.com/photos/grey-reusable-bottle-on-grey-background-picture-id1299291084?b=1&k=20&m=1299291084&s=612x612&w=0&h=hGhYT35eg9QMS7PymtPdWN9_y9GCeks5Nr7MUjFj3D0=";
        obj=findViewById(R.id.OBJ);
        obj.setText(name);
        def=findViewById(R.id.DEF);
        def.setText(deff);
        //FindIMG
        labels = new Vector<>();
        try {
            String actualfile = "objectlabelmap.txt";
            InputStream labelsInput = getAssets().open(actualfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
            String linkfile = "samplephotolink.txt";
            InputStream linkinput = getAssets().open(linkfile);
            BufferedReader cr = new BufferedReader(new InputStreamReader(linkinput));
            String line=br.readLine();
            String tmplink=cr.readLine();
            while ((line)!=null){
                if(line.equals(name)) {
                    link=tmplink;
                    break;
                }
                else {
                    line=br.readLine();
                    tmplink=cr.readLine();
                }

            }
            br.close();
            cr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pic = findViewById(R.id.WEB);
        pic.loadUrl(link);
    }
    public void go_back(View view){
        finish();
    }
}