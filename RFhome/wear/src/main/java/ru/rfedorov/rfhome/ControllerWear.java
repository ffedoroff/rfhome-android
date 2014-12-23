package ru.rfedorov.rfhome;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.rfedorov.wear_tools.BaseController;

public class ControllerWear extends BaseController {
    private static final String TAG = "ControllerWear";
    private static final ControllerWear singleton = new ControllerWear();
    public Fragment4 mainActivity;
    private List<String> model;

    private ControllerWear() {
        Log.v(TAG, "Controller created");
        Init(true);
        model = new ArrayList<>();
        sendInitRequestToMobile();
    }

    public static ControllerWear getInstance() {
        return singleton;
    }

    @Override
    public void onMessageFromMobile(String data) {
        String[] adata = data.split(",");
//        Log.v(TAG, "onMessageFromMobile " + data);
        if (adata.length > 1 && "set-values".equals(adata[0])) {
            getModel().clear();
            for (int i = 1; i < adata.length; i++) {
                getModel().add(adata[i]);
            }
            onModelChanged();
        }
    }

    public void sendInitRequestToMobile() {
        Log.v(TAG, "sendInitMessage");
        SendToMobileAsync("init");
    }

    public void sendClickToMobile(String data) {
        Log.v(TAG, "sendClickToMobile");
        SendToMobileAsync("click," + data);
    }

    public void onModelChanged() {
        Log.i(TAG, "onModelChanged");
        if (mainActivity != null) mainActivity.UpdateView();
    }

    public List<String> getModel() {
        return model;
    }
}
