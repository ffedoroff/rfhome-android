package ru.rfedorov.wear_tools;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public abstract class BaseController
        extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "BaseController";
    private static final String msgPathMobile = "/rf_mobile";
    private static final String msgPathWearable = "/rf_wear";
    private GoogleApiClient googleClient;

    protected BaseController() {
//        Log.v(TAG, "Controller created");
    }

    protected void Init(Boolean isWearable) {
        Log.v(TAG, "Init");
        BaseListener.bindController(this);
        googleClient = new GoogleApiClient.Builder(getAppContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();
    }

    public Context getAppContext() {
        return BaseApplication.getAppContext();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //Log.e(TAG, "onMessageReceived");
        String path = messageEvent.getPath();
        String data = new String(messageEvent.getData());
        switch (path) {
            case msgPathMobile:
                onMessageFromWearable(data);
                break;
            case msgPathWearable:
                onMessageFromMobile(data);
                break;
            default:
                Log.e(TAG, "Unknown message path=" + path + " data=" + data);
                break;
        }
    }

    public void onMessageFromMobile(String data) {
    }

    public void onMessageFromWearable(String data) {
    }

    public void SendToWearableAsync(String data) {
        new WearableSender(data, msgPathWearable).start();
    }

    public void SendToMobileAsync(String data) {
        new WearableSender(data, msgPathMobile).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "mobile onConnectionSuspended cause:" + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "mobile onConnectionFailed result:" + connectionResult);
    }

    class WearableSender extends Thread {
        String message;
        String path;

        // Constructor to send a message to the data layer
        WearableSender(String message, String path) {
            this.message = message;
            this.path = path;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
//                String msgPath = isWear ? msgPathMobile : msgPathWearable;
                MessageApi.SendMessageResult result =
                        Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to: " + path + " " + node.getDisplayName());
                } else {
                    Log.e(TAG, "ERROR: failed to send Message to watch: {" + message + "} watch: " + node.getDisplayName());
                }
            }
        }
    }
}
