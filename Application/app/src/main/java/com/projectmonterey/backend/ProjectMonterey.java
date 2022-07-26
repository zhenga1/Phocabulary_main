package com.projectmonterey.backend;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProjectMonterey extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        FileInputStream refreshToken = null;
//        try {
//            refreshToken = new FileInputStream("path/to/refreshToken.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        if(refreshToken!=null) {
//            FirebaseOptions.Builder builder = new FirebaseOptions.Builder();
//            FirebaseOptions options = builder.setCredentials(GoogleCredentials.getApplicationDefault())
//                    .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
//                    .build();
//        }
        FirebaseApp.initializeApp(this);
        initialiseActivityCallback();
    }

    private void initialiseActivityCallback() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


// for each activity this function is called and so it is set to portrait mode


            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

}
