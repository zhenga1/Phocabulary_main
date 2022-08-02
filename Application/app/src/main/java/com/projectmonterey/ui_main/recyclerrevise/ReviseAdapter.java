package com.projectmonterey.ui_main.recyclerrevise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectmonterey.R;

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
        //TextView object = child.findViewById(R.id.OBJ);
        String link="";
        TextView object = holder.object;
        TextView definition = holder.definition;
        object.setText(WordsRevise.labels.get(position));
        definition.setText(WordsRevise.labeldefitions.get(position));
        link=WordsRevise.links.get(position);
        WebView webView = holder.image;
        webView.loadUrl(link);
        Log.d(this.toString(),Integer.toString(position));

    }

    @Override
    public int getItemCount() {
        return WordsRevise.labels.size();
    }

    public class ReviseViewHolder extends RecyclerView.ViewHolder{
        public TextView object, definition;
        public WebView image;
        public ReviseViewHolder(@NonNull View itemView) {
            super(itemView);
            object = itemView.findViewById(R.id.OBJ);
            definition = itemView.findViewById(R.id.DEF);
            image = itemView.findViewById(R.id.WEB);
        }
    }
}
