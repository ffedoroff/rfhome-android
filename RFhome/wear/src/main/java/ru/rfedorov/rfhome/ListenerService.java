package ru.rfedorov.rfhome;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {
    private static final String TAG = "ListenerServiceWear";
    private static final String msgPath = "/rfedorov_wear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(msgPath)) {
            final String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("wear", true);
            messageIntent.putExtra("data", message);
            Log.v(TAG, "onMessageReceived: " + message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else {
            Log.e(TAG, "onMessageReceived not processed: " + messageEvent.getPath());
            super.onMessageReceived(messageEvent);
        }
    }
}
