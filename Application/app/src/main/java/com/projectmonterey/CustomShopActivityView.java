package com.projectmonterey;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CustomShopActivityView extends FrameLayout {
    Context context;
    View child;
    public CustomShopActivityView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomShopActivityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public CustomShopActivityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }
    public void initView(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        child = inflater.inflate(R.layout.shop_item_view,this);
    }
    public void editTitle(String title){
        if(child!=null){
            TextView text = child.findViewById(R.id.title_of_prize);
            text.setText(title);
        }
    }
    public void editCost(String cost){
        if(child!=null){
            TextView text = child.findViewById(R.id.price);
            text.setText(cost);
        }
    }
}
