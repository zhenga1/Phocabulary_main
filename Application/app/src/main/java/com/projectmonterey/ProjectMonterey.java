package com.projectmonterey;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class ProjectMonterey extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
    }
}
