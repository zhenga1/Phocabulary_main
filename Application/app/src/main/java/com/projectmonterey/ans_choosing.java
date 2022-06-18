package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

import android.content.Intent;
import android.widget.Button;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ans_choosing extends AppCompatActivity {
    public Integer[] rn={0,1,2};
    private String[] cho={"A","B","C"};
    //cho[0]=correct ans
    //cho[1]=wrong_ans1
    //cho[2]=wrong_ans2
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
    }
    public void next(){
        //save data
        Intent intent = new Intent(this, ans_choosing.class);
        startActivity(intent);
        ans_choosing.this.finish();
    }
    public void ans_A(View view){
        if (rn[0]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            Button button = findViewById(R.id.ansa);
            button.setEnabled(false);
        }
        else{
            next();
        }
    }
    public void ans_B(View view){
        if (rn[1]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            Button button = findViewById(R.id.ansb);
            button.setEnabled(false);
        }
        else{
            next();
        }
    }
    public void ans_C(View view){
        if (rn[2]!=0){
            Toast.makeText(getApplicationContext(),"Wrong answer. Please try again.",Toast.LENGTH_SHORT).show();
            Button button = findViewById(R.id.ansc);
            button.setEnabled(false);
        }
        else {
            next();
        }
    }
}