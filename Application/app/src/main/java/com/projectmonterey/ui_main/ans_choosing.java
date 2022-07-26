package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

import android.content.Intent;
import android.webkit.WebView;
import android.widget.Button;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.projectmonterey.R;

public class ans_choosing extends AppCompatActivity {
    public Integer[] rn={0,1,2};
    private List<Integer> indexArray;
    private String[] cho = new String[3];
    //cho[0]=correct ans
    //cho[1]=wrong_ans1
    //cho[2]=wrong_ans2
    TextView ans;
    Button nxt,shw;
    WebView pic;
    public Vector<String> labels = new Vector<>();
    public Vector<String> uriImages = new Vector<>();
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    String link = "https://www.supereasy.com/wp-content/uploads/2018/08/img_5b7fd932ba802.png";
    private boolean first=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans_choosing);
        readLabelFile();
        initialiseChoice();
        List<Integer> list = Arrays.asList(rn);
        Collections.shuffle(list);
        list.toArray(rn);
        Button button = findViewById(R.id.ansa);
        button.setText(cho[rn[0]]);
        Button button1 = findViewById(R.id.ansb);
        button1.setText(cho[rn[1]]);
        Button button2 = findViewById(R.id.ansc);
        button2.setText(cho[rn[2]]);
        //set photo
        ans=findViewById(R.id.Ans);
        ans.setVisibility(View.INVISIBLE);
        nxt=findViewById(R.id.NEXT);
        nxt.setVisibility(View.INVISIBLE);

        shw=findViewById(R.id.SHOWANS);
        pic = findViewById(R.id.PIC);
        pic.loadUrl(link);
    }

    private void initialiseChoice() {
        if(first) {
            indexArray = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                indexArray.add(i);
            }
            first=false;
        }
        Collections.shuffle(indexArray);
        cho[0] = labels.get(indexArray.get(0));
        cho[1] = labels.get(indexArray.get(1));
        cho[2] = labels.get(indexArray.get(2));
        link = uriImages.get(indexArray.get(0));
    }

    private void readLabelFile(){
        String labelfile = "objectlabelmap.txt";
        String linkFile = "samplephotolink.txt";
        try{
            InputStream labelsInput = getAssets().open(labelfile);
            InputStream linkInput = getAssets().open(linkFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
            BufferedReader nbr = new BufferedReader(new InputStreamReader(linkInput));
            String line,link;
            line=br.readLine();
            link=nbr.readLine();
            while(line!=null){
                if(!line.equals( "???" )){
                    labels.add(line);
                    uriImages.add(link);
                }
                line=br.readLine();
                link=nbr.readLine();
            }
            br.close();
            nbr.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void go_back(View view){ans_choosing.this.finish();}
    public void nextt(View view){
        Intent intent = new Intent(this, ans_choosing.class);
        startActivity(intent);
        ans_choosing.this.finish();
    }
    public void show(View view){
        Button button = findViewById(R.id.ansa);
        button.setEnabled(false);
        Button buttonb = findViewById(R.id.ansb);
        buttonb.setEnabled(false);
        Button buttonc = findViewById(R.id.ansc);
        buttonc.setEnabled(false);
        next();
    }
    public void next(){
        shw.setVisibility(View.INVISIBLE);
        //save data
        Button button = findViewById(R.id.ansa);
        button.setEnabled(false);
        Button buttonb = findViewById(R.id.ansb);
        buttonb.setEnabled(false);
        Button buttonc = findViewById(R.id.ansc);
        buttonc.setEnabled(false);
        ans.setText("The answer is: "+cho[0]);
        ans.setVisibility(View.VISIBLE);
        nxt.setVisibility(View.VISIBLE);

    }
    public void ans_A(View view){
        Button button = findViewById(R.id.ansa);
        button.setEnabled(false);
        if (rn[0]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            button.setBackgroundColor(0xffff0000);
        }
        else {
            button.setBackgroundColor(0xff00ff00);
            next();
        }
    }
    public void ans_B(View view){
        Button button = findViewById(R.id.ansb);
        button.setEnabled(false);
        if (rn[1]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            button.setBackgroundColor(0xffff0000);
        }
        else {
            button.setBackgroundColor(0xff00ff00);
            next();
        }
    }
    public void ans_C(View view){
        Button button = findViewById(R.id.ansc);
        button.setEnabled(false);
        if (rn[2]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            button.setBackgroundColor(0xffff0000);
        }
        else {
            button.setBackgroundColor(0xff00ff00);
            next();
        }
    }
}