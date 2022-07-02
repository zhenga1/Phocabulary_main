package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import android.content.Intent;
import android.widget.Button;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.projectmonterey.R;

public class ans_choosing extends AppCompatActivity {
    public Integer[] rn={0,1,2};
    private String[] cho={"A","B","C"};
    //cho[0]=correct ans
    //cho[1]=wrong_ans1
    //cho[2]=wrong_ans2
    TextView ans;
    Button nxt,shw;

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
    }
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
        ans.setText(cho[0]);
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
            button.setBackgroundColor(0xf000fff0);
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
            button.setBackgroundColor(0xf000fff0);
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
            button.setBackgroundColor(0xf000fff0);
            next();
        }
    }
}