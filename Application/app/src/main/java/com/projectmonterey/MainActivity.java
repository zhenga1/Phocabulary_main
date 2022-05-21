package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void learn(View view) {
        Intent intent = new Intent(this,UiMainActivity.class);
        startActivity(intent);
    }

    public void setting(View view){
        //Intent intent = new Intent(this,Uisetting.class);
        //startActivity(intent);
    }
}
