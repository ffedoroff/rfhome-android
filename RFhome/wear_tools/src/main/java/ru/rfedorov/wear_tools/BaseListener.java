package ru.rfedorov.wear_tools;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class BaseListener extends WearableListenerService {
    private static final String TAG = "BaseListener";
    private static BaseController controller = null;

    public BaseListener() {
    }

    public static void bindController(BaseController controller) {
        Log.v(TAG, "bindController");
        if (BaseListener.controller != null) {
            Log.wtf(TAG, "bindController already initialized");
            //throw new Exception("bindController already initialized");
        }
        BaseListener.controller = controller;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (controller != null)
            controller.onMessageReceived(messageEvent);
        else
            Log.e(TAG, "message received but controller is empty");
    }
}
