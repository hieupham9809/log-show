package com.example.logshowjava;

import android.app.Application;

import timber.log.Timber;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new FileLoggingTree(getApplicationContext()));

    }
}
