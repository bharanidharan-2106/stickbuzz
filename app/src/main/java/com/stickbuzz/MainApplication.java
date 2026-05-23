package com.stickbuzz;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
