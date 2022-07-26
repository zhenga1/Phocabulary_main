package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

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
    private String[] cho={"Bottle","Computer","Tree"};
    //cho[0]=correct ans
    //cho[1]=wrong_ans1
    //cho[2]=wrong_ans2
    TextView ans;
    Button nxt,shw;
    WebView pic;
    private static final String LABELS_FILE = "file:///android_asset/objectlabelmap.txt";
    private static final String DEFINITION_FILE = "file:///android_asset/objectdefinitions.txt";
    String link = "https://media.istockphoto.com/photos/grey-reusable-bottle-on-grey-background-picture-id1299291084?b=1&k=20&m=1299291084&s=612x612&w=0&h=hGhYT35eg9QMS7PymtPdWN9_y9GCeks5Nr7MUjFj3D0=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans_choosing);
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