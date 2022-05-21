package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.List;

public class Settings extends AppCompatActivity {
    public List<LinearLayout> linearLayoutList;
    public LinearLayout mainLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mainLL = findViewById(R.id.settingsLL);

    }
}