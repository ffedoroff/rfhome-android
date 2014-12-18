package ru.rfedorov.rfhome;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class RFApplication extends Application {
    private static final String TAG = "RFApplication";

    private static Context context;

    public void onCreate() {
        Log.v(TAG, "RFApplication onCreate");
        super.onCreate();
        RFApplication.context = getApplicationContext();
        Controller.getInstance();
    }

    public static Context getAppContext() {
        return RFApplication.context;
    }
}