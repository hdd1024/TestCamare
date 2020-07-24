package com.example.testcamare;

import android.app.Application;

import com.example.testcamare.serialport.SerialPortHelper;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SerialPortHelper.instance().open();
    }
}
