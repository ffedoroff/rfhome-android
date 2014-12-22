package ru.rfedorov.rfhome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class Controller implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Controller Wear";
    private static final String msgPath = "/rfedorov_mobile";
    GoogleApiClient googleClient;
    private static Controller ourInstance = new Controller();
    private List<String> model;
    public Fragment4 mainActivity;

    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {
        Log.v(TAG, "Controller created");
        model = new ArrayList<>();

        // Register the local broadcast receiver (listens wearable)
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MobileReceiver wearableReceiver = new MobileReceiver();
        LocalBroadcastManager.getInstance(RFApplication.getAppContext()).registerReceiver(wearableReceiver, messageFilter);

        googleClient = new GoogleApiClient.Builder(RFApplication.getAppContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

        //sendInitRequestToMobile();
    }

    protected void reLoadUnitsFromString(String str) {
        Log.v(TAG, "reLoadUnitsFromString");
        String[] strArray = str.split(",");
        if (strArray.length > 1 && strArray[0].equals("set-values")) {
            getModel().clear();
            for (int i=1; i<strArray.length; i++) {
                getModel().add(strArray[i]);
            }
            onModelChanged();
        }
        //        } catch (JSONException e) {
        //            Log.e(TAG, "JSONException", e);
        //        }
    }

    private void sendInitRequestToMobile() {
        Log.v(TAG, "sendInitMessage");
        new MobileSender("init").start();
    }

    public void onModelChanged() {
        Log.i(TAG, "onModelChanged");
        if (mainActivity != null) mainActivity.UpdateView();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
        sendInitRequestToMobile();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "mobile onConnectionSuspended cause:" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "mobile onConnectionFailed result:" + connectionResult);
    }

    public List<String> getModel() {
        return model;
    }

    private class MobileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("wear", false)) {
                String data = intent.getStringExtra("data");
                Log.v(TAG, "MessageReceiver onReceive "+data);
                if (!data.isEmpty()) {
                    reLoadUnitsFromString(data);
                }
            }
        }
    }

    class MobileSender extends Thread {
        String message;

        // Constructor to send a message to the data layer
        MobileSender(String msg) {
            message = msg;
        }
        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), msgPath, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    Log.e(TAG, "ERROR: failed to send Message to watch: {" + message + "} watch: " + node.getDisplayName());
                }
            }
        }
    }
}
