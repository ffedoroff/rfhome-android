package ru.rfedorov.rfhome;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class RFApplication extends Application {
    private static final String TAG = "RFApplication";

    private static Context context;

    public void onCreate() {
        Log.v(TAG, "RFApplication onCreate");
        super.onCreate();
        RFApplication.context = getApplicationContext();
        //PreferenceManager.setDefaultValues(getAppContext(), R.layout.settings, false);
        Controller.getInstance();
    }

    public static Context getAppContext() {
        return RFApplication.context;
    }
}
