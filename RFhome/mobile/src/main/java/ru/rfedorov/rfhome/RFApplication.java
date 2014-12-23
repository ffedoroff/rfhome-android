package ru.rfedorov.rfhome;

import ru.rfedorov.wear_tools.BaseApplication;

public class RFApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Controller.getInstance().Init(getApplicationContext(), false);
    }
}
