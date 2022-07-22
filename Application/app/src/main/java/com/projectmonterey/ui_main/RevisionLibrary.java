package com.projectmonterey.ui_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.View;

import com.projectmonterey.R;
import com.projectmonterey.ui_main.recyclerrevise.WordsRevise;

public class RevisionLibrary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_library);
    }
    public void reviseallwords(View view){
        Intent intent = new Intent(RevisionLibrary.this, WordsRevise.class);
        startActivity(intent);
    }
    public void finishact(View view){finish();}
}