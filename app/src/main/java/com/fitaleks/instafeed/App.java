package com.fitaleks.instafeed;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by alexanderkulikovskiy on 02.07.15.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
