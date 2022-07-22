package com.projectmonterey.ui_main.recyclerrevise;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Bundle;
import android.view.View;

import com.projectmonterey.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class WordsRevise extends AppCompatActivity {
    protected RecyclerView recyclerView;
    protected LinearLayoutManager linearLayoutManager;
    public static Vector<String> labels = new Vector<>();
    public static Vector<String> labeldefitions = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_revise);

        labels = new Vector<>();
        labeldefitions = new Vector<>();
        try {
            String actualfile = "objectlabelmap.txt";
            InputStream labelsInput = getAssets().open(actualfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
            String line=br.readLine();
            while ((line)!=null){
                if(!line.equals("???")) {
                    WordsRevise.labels.add(line);
                }
                line=br.readLine();
            }
            String deffile = "objectdefinitions.txt";
            InputStream definitionInput = getAssets().open(deffile);
            BufferedReader nbr = new BufferedReader(new InputStreamReader(definitionInput));
            line=nbr.readLine();
            while ((line)!=null){
                if(!line.equals("???")) {
                    WordsRevise.labeldefitions.add(line);
                }
                line= nbr.readLine();
            }
            br.close();
            nbr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initAdapter();
    }

    private void initAdapter() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        ReviseAdapter reviseAdapter = new ReviseAdapter(getApplicationContext());
        recyclerView.setAdapter(reviseAdapter);
    }
    public void finishacttwo(View view){finish();}

}