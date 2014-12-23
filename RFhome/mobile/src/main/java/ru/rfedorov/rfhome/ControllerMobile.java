package ru.rfedorov.rfhome;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import ru.rfedorov.wear_tools.BaseController;

public class ControllerMobile extends BaseController {
    private static final String TAG = "ControllerMobile";
    private static final ControllerMobile singleton = new ControllerMobile();
    public MainActivity mainActivity;
    private ModelRFHome model;

    private ControllerMobile() {
        Log.v(TAG, "Controller created");
        Init(false);
        model = new ModelRFHome("0");
        reloadFromServer();
    }

    public static ControllerMobile getInstance() {
        return singleton;
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
                        if ("true_time".equals(key))
                            _munit.setLastTrueValueTime(Long.parseLong(value));
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
        for (ModelUnit unit : getModel().getPrimeUnits().values()) {
            message += "," + unit.getPrimeUnitTitle();
        }
        this.SendToWearableAsync(message);
//        new WearableSender(message).start();
        Log.v(TAG, "init response " + message);
    }

    public void PostUnitUpdate(String unitName, String newValue) {
        new APIConnector().execute(AsyncApiCall.API_POST_MESSAGE, unitName, newValue);
    }

    public void onModelChanged() {
        Log.i(TAG, "onModelChanged " + getModel().getUnits().size());
        sendInitResponseToWearable();
        if (mainActivity != null) mainActivity.reCreateUnits();
    }

    public ModelRFHome getModel() {
        return model;
    }

    public void onMessageFromWearable(String data) {
        String[] adata = data.split(",");
        Log.v(TAG, "onMessageFromWearable onReceive " + data);
        if (adata.length > 0) {
            if (adata.length == 2 && "click".equals(adata[0])) {
                if (getModel().getPrimeUnits().containsKey(adata[1])) {
                    ModelUnit unit = getModel().getPrimeUnits().get(adata[1]);
                    Log.v(TAG, "onMessageFromWearable click " + unit.getName());
                    PostUnitUpdate(unit.getName(), String.valueOf(!unit.isTrue()));
                }
            } else if (adata.length == 1 && "init".equals(adata[0])) {
                sendInitResponseToWearable();
            }
        }
    }

    class APIConnector extends AsyncApiCall {
        @Override
        public void onResult(String command, Boolean result, String data) {
            Log.i(TAG, "onResult " + command + " " + result);
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
