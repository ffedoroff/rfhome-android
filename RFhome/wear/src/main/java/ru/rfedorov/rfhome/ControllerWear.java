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
        //sendInitRequestToMobile();
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

//    protected void reLoadUnitsFromString(String str) {
//        Log.v(TAG, "reLoadUnitsFromString");
//        String[] strArray = str.split(",");
//        if (strArray.length > 1 && strArray[0].equals("set-values")) {
//            getModel().clear();
//            for (int i = 1; i < strArray.length; i++) {
//                getModel().add(strArray[i]);
//            }
//            onModelChanged();
//        }
//    }

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

//    private class MobileReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getBooleanExtra("wear", false)) {
//                String data = intent.getStringExtra("data");
//                Log.v(TAG, "MessageReceiver onReceive " + data);
//                if (!data.isEmpty()) {
//                    reLoadUnitsFromString(data);
//                }
//            }
//        }
//    }

//    class MobileSender extends Thread {
//        String message;
//
//        // Constructor to send a message to the data layer
//        MobileSender(String msg) {
//            message = msg;
//        }
//
//        public void run() {
//            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
//            for (Node node : nodes.getNodes()) {
//                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), msgPath, message.getBytes()).await();
//                if (result.getStatus().isSuccess()) {
//                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
//                } else {
//                    Log.e(TAG, "ERROR: failed to send Message to watch: {" + message + "} watch: " + node.getDisplayName());
//                }
//            }
//        }
//    }
}
