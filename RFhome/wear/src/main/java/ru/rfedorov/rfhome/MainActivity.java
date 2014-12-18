package ru.rfedorov.rfhome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivityWear";
    private Button[] buttons;
    private static final String msgPathMobile = "/rfedorov_mobile";

    GoogleApiClient googleClient;

//    private static final int[] buttons = new int[] { R.id.btn1, R.id.btn1, R.id.btn1, R.id.btn1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build a new GoogleApiClient
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleClient.connect();

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                buttons = new Button[] {
                        (Button)findViewById(R.id.btn1),
                        (Button)findViewById(R.id.btn2),
                        (Button)findViewById(R.id.btn3),
                        (Button)findViewById(R.id.btn4)
                };
            }
        });
        sendInitMessage();
    }

    public void onBtnClicked(View view) {
        if (!(view.getTag() instanceof Boolean)) view.setTag(false);

        Boolean val = (Boolean)view.getTag();
        if (val) {
            view.setBackgroundResource(R.drawable.bulb_off);
        } else {
            view.setBackgroundResource(R.drawable.bulb_on);
        }
        view.setTag(!val);

//        if view.getBackground() == R.drawable.bulb_on
        new SendToDataLayerThread("click,"+((Button)view).getText()).start();
    }

    private void sendInitMessage() {
        Log.v(TAG, "sendInitMessage");
        new SendToDataLayerThread("init").start();
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            String[] data = msg.split(",");
            if (data.length > 1) {
                if ("set-values".equals(data[0])) {
                    for (int i=1; i<Math.min(data.length, buttons.length+1); i++) {
                        buttons[i-1].setText(data[i]);
                        buttons[i-1].setBackgroundResource(R.drawable.bulb_off);
                        buttons[i-1].setTag(false);
                    }
                }
            }
        }
    }

    // Send a message when the data layer connection is successful.
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "onConnected");
        sendInitMessage();
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "wear onConnectionSuspended cause:"+cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "wear onConnectionFailed result:"+connectionResult);
    }

    class SendToDataLayerThread extends Thread {
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String msg) {
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), msgPathMobile, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to phone: " + node.getDisplayName());
                }
                else {
                    // Log an error
                    Log.e(TAG, "ERROR: failed to send Message to phone: {" + message + "} watch: " + node.getDisplayName());
                }
            }
        }
    }

}

