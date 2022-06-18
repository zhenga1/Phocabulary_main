package com.projectmonterey;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ShopActivity extends AppCompatActivity {
    public LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        linearLayout = findViewById(R.id.shopLL);
        CustomShopActivityView view1 = new CustomShopActivityView(this);
        view1.editCost("$0");
        CustomShopActivityView view2 = new CustomShopActivityView(this);
        view2.editTitle("Two Random Categories For Free!!");
        view2.editCost("$20");
        CustomShopActivityView view3 = new CustomShopActivityView(this);
        view3.editTitle("Three Random Categories For Free!!");
        view3.editCost("$30");
        CustomShopActivityView view4 = new CustomShopActivityView(this);
        view4.editTitle("Four Random Categories For Free!!");
        view4.editCost("$40");
        linearLayout.addView(view1);
        linearLayout.addView(view2);
        linearLayout.addView(view3);
        linearLayout.addView(view4);
    }
    public void back(View view){
        finish();
    }
}