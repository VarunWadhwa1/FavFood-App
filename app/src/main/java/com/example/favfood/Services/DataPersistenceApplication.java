package com.example.favfood.Services;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class DataPersistenceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}