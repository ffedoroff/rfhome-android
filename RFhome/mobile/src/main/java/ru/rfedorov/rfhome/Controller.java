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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Controller implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Controller";
    private static final String msgPathWearable = "/rfedorov_watch";
    GoogleApiClient googleClient;
    private static Controller ourInstance = new Controller();
    private ModelRFHome model;
    public MainActivity mainActivity;

    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {
        Log.v(TAG, "Controller created");
        model = new ModelRFHome("0");

        // Register the local broadcast receiver (listens wearable)
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        WearableReceiver wearableReceiver = new WearableReceiver();
        LocalBroadcastManager.getInstance(RFApplication.getAppContext()).registerReceiver(wearableReceiver, messageFilter);

        googleClient = new GoogleApiClient.Builder(RFApplication.getAppContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleClient.connect();

        reloadFromServer();
    }

    public void reloadFromServer() {
        new APIConnector().execute(AsyncApiCall.API_GET_JSON, "0");
    }


    protected void reLoadUnitsFromJson(String json) {
        try {
            JSONObject json_reader = new JSONObject(json);
            String lastCall = "0";
            JSONObject meta = json_reader.optJSONObject("meta");
            if (meta != null) {
                String _lastCall = meta.optString("export_time");
                if (_lastCall != null && !_lastCall.isEmpty()) {
                    lastCall = _lastCall;
                    Log.v(TAG, "lastCall " + lastCall);
                }
            }
            ModelRFHome _model = new ModelRFHome(lastCall);

            JSONArray sections = json_reader.getJSONArray("sections");
            for (int s = 0; s < sections.length(); s++) {
                JSONObject j_section = sections.getJSONObject(s);
                ModelSection m_section = new ModelSection(j_section.getString("name"));
                _model.getSections().add(m_section);

                JSONArray units = j_section.getJSONArray("units");
                for (int i = 0; i < units.length(); i++) {
                    ModelUnit _munit = new ModelUnit();
                    JSONObject unit = units.getJSONObject(i);
                    Iterator<String> iterator = unit.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = unit.get(key).toString();
                        if ("unit_type".equals(key)) _munit.setUnitType(value);
                        if ("name".equals(key)) _munit.setName(value);
                        if ("false_time".equals(key))
                            _munit.setLastFalseValueTime(Long.parseLong(value));
                        if ("true_time".equals(key)) _munit.setLastTrueValueTime(Long.parseLong(value));
                        //if ("section".equals(key)) sectionName = value;
                        if ("prime_unit_title".equals(key)) _munit.setPrimeUnitTitle(value);
                    }
                    if (!_munit.isValid()) {
                        Log.e(TAG, "Wrong unit in json: " + unit);
                        continue;
                    }
                    if (_munit.getPrimeUnitTitle() != null && !_munit.getPrimeUnitTitle().isEmpty()) {
                        _model.getPrimeUnits().put(_munit.getPrimeUnitTitle(), _munit);
                    }
                    m_section.getUnits().add(_munit);
                    _model.getUnits().put(_munit.getName(), _munit);
                }
            }
            model = _model;
            onModelChanged();
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
        }
    }

    private void sendInitResponseToWearable() {
        String message = "set-values";
        for (ModelUnit unit: getModel().getPrimeUnits().values()) {
            message += ","+unit.getPrimeUnitTitle();
        }
        new WearableSender(message).start();
        Log.v(TAG, "init response " + message);
    }

    public void PostUnitUpdate(String unitName, String newValue) {
        new APIConnector().execute(AsyncApiCall.API_POST_MESSAGE, unitName, newValue);
    }
    public void onModelChanged() {
        Log.i(TAG, "onModelChanged "+getModel().getUnits().size());
        sendInitResponseToWearable();
        if (mainActivity != null) mainActivity.reCreateUnits();
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

    public ModelRFHome getModel() {
        return model;
    }

    private class WearableReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("wearable", false)) {
                String[] data = intent.getStringExtra("data").split(",");
                Log.v(TAG, "MessageReceiver onReceive "+intent.getStringExtra("data"));
                if (data.length > 0) {
                    if (data.length == 2 && "click".equals(data[0])) {
                        if (getModel().getPrimeUnits().containsKey(data[1])) {
                            ModelUnit unit = getModel().getPrimeUnits().get(data[1]);
                            Log.v(TAG, "MessageReceiver click " + unit.getName());
                            PostUnitUpdate(unit.getName(), String.valueOf(!unit.isTrue()));
                        }
                    } else if (data.length == 1 && "init".equals(data[0])) {
                        sendInitResponseToWearable();
                    }
                }
            }
        }
    }

    class WearableSender extends Thread {
        String message;

        // Constructor to send a message to the data layer
        WearableSender(String msg) {
            message = msg;
        }
        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), msgPathWearable, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    Log.e(TAG, "ERROR: failed to send Message to watch: {" + message + "} watch: " + node.getDisplayName());
                }
            }
        }
    }

    class APIConnector extends AsyncApiCall {
        @Override
        public void onResult(String command, Boolean result, String data) {
            Log.i(TAG, "onResult "+command+" "+result);
            if (result) {
                if (API_GET_JSON.equals(command)) {
                    reLoadUnitsFromJson(data);
                } else if (API_POST_MESSAGE.equals(command)) {
                    new APIConnector().execute(AsyncApiCall.API_GET_JSON, "0");
                }
            }
        }
    }
}
