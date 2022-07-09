package com.projectmonterey.livedetect.object_detection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


@SuppressLint("AppCompatCustomView")
public class CustomGif extends WebView {
    String urlGif;
    int urlid = 301431325;
    Context context;
    public CustomGif(Context context) {
        super(context);
        this.context = context;
    }

    public CustomGif(Context context, String url){
        super(context);
        this.urlGif = url;

    }
    public CustomGif(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomGif(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
    public void setUrlGif(@NonNull int resourceid){
        this.urlid = resourceid;
    }
    public void setUrlGif(@NonNull String resourceid){
        this.urlGif = resourceid;
    }
    public void configureView(){
        if(urlGif==null && urlid==301431325){
            Toast.makeText(context.getApplicationContext(),"BUG: The url of the horray gif is not defined, please contact the developer.",Toast.LENGTH_SHORT).show();
            this.setVisibility(View.GONE);
            return;
        }
        this.loadUrl(urlGif);
            /*Glide.with(context)
                    .asGif()
                    .load(urlGif)
                    .into(this);*/
        this.setVisibility(View.VISIBLE);
    }

}
