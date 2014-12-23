package ru.rfedorov.wear_tools;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BaseApplication extends Application {
    private static final String TAG = "RFApplicationBase";
//    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "RFApplication onCreate");
        //BaseApplication.context = getApplicationContext();
        //PreferenceManager.setDefaultValues(getAppContext(), R.layout.settings, false);
        //Controller.getInstance();
    }

//    public static Context getAppContext() {
//        return BaseApplication.context;
//    }
}