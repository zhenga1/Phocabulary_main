package com.projectmonterey.ui_main.recyclerrevise;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectmonterey.R;
import com.projectmonterey.livedetect.classifiers.ObjectDetectionClassifier;

public class ReviseAdapter extends RecyclerView.Adapter<ReviseAdapter.ReviseViewHolder> {
    public int indexOfWord = 0;
    public View parent, child;
    public String wordString,wordDef;

    public ReviseAdapter(Context context){
        this.parent = new View(context);
        this.child = new View(context);
//        this.indexOfWord = index;
//        this.wordDef = ObjectDetectionClassifier.labeldefitions.get(indexOfWord);
//        this.wordString = ObjectDetectionClassifier.labels.get(indexOfWord);

    }
    public ReviseAdapter(String wordString){
        this.wordString = wordString;
        try {
            this.indexOfWord = WordsRevise.labels.indexOf(wordString);
            this.wordDef = WordsRevise.labeldefitions.get(indexOfWord);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public ReviseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        child = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerlayoutview,parent,false);
        ReviseViewHolder reviseViewHolder = new ReviseViewHolder(child);

        return reviseViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ReviseViewHolder holder, int position) {
        //int index = holder.getAdapterPosition();
        TextView object = child.findViewById(R.id.OBJ);
        TextView definition = child.findViewById(R.id.DEF);
        object.setText(wordString != null? wordString : WordsRevise.labels.get(position));
        definition.setText(wordDef != null? wordDef : WordsRevise.labeldefitions.get(position));
        String link= "https://media.istockphoto.com/photos/grey-reusable-bottle-on-grey-background-picture-id1299291084?b=1&k=20&m=1299291084&s=612x612&w=0&h=hGhYT35eg9QMS7PymtPdWN9_y9GCeks5Nr7MUjFj3D0=";
        WebView webView = child.findViewById(R.id.WEB);
        webView.loadUrl(link);
        Log.d(this.toString(),Integer.toString(position));

    }

    @Override
    public int getItemCount() {
        return WordsRevise.labels.size();
    }

    public class ReviseViewHolder extends RecyclerView.ViewHolder{

        public ReviseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
