package com.testography.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DevintensiveApplication extends Application {

    private static Context sContext;
    public static SharedPreferences sSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {
        return sContext;
    }
}
