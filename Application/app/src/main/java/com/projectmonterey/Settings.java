package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        //Should add the customised linearlayout views
        LinearLayout linearLayout = new LinearLayout(Settings.this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ImageView imageView = new ImageView(Settings.this);

    }
}