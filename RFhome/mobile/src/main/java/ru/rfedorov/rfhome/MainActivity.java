package ru.rfedorov.rfhome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivityMobile";
    private static final int RESULT_SETTINGS = 1;

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        ControllerMobile.getInstance().mainActivity = this;
        reCreateUnits();
        Log.i(TAG, "onStart");
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        super.onStop();
        ControllerMobile.getInstance().mainActivity = null;
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
//        }
    }

    private void calcButtonStatus(Button btn) {
        int drawable_id = R.drawable.btn_blue;
//        int icon_id = R.drawable.bulb_off;
        if (((ModelUnit) btn.getTag()).isTrue()) {
//            icon_id = R.drawable.bulb_on;
            drawable_id = R.drawable.btn_gold;
        }
//        Drawable icon = getApplicationContext().getResources().getDrawable(icon_id);
//        btn.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        btn.setBackgroundResource(drawable_id);
    }

    public void reCreateUnits() {
        LinearLayout buttons_layout = (LinearLayout) findViewById(R.id.buttons_layout);
        buttons_layout.removeAllViewsInLayout();
        for (ModelSection section : ControllerMobile.getInstance().getModel().getSections()) {
            for (ModelUnit unit : section.getUnits()) {
                LinearLayout layout = new LinearLayout(buttons_layout.getContext(), null, R.style.linearForButtons);
                Button btn = new Button(buttons_layout.getContext());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                btn.setLayoutParams(params);
                btn.setTag(unit);
                btn.setText(unit.getName());
                btn.setTextSize(25);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTriggerClicked(v);
                    }
                });
                calcButtonStatus(btn);
                layout.addView(btn);
                buttons_layout.addView(layout);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.refresh:
                ControllerMobile.getInstance().reloadFromServer();
                break;
            case R.id.action_settings:
                Intent userSettingIntent = new Intent(this, UserSettings.class);
                startActivityForResult(userSettingIntent, RESULT_SETTINGS);
                break;
            case R.id.action_about:
                Intent aboutUsIntent = new Intent(this, AboutUs.class);
                startActivity(aboutUsIntent);
                break;
        }
        return true;
    }

    public void onTriggerClicked(View view) {
        ModelUnit unit = (ModelUnit) view.getTag();
        ControllerMobile.getInstance().PostUnitUpdate(unit.getName(), String.valueOf(!unit.isTrue()));
    }
}
