/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.rfedorov.rfhome;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class Fragment4 extends Fragment {
    private static final String TAG = "Fragment4";
    Button[] buttons = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4, container, false);
    }

    public void UpdateView() {
        Log.i(TAG, "UpdateView");
        List<String> strings = ControllerWear.getInstance().getModel();
        for (int i = 0; i < 4; i++) {
            if (i < strings.size())
                buttons[i].setText(strings.get(i));
            buttons[i].setBackgroundResource(R.drawable.bulb_off);
            buttons[i].setTag(false);
        }
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    public void onStop() {
        super.onStop();
        ControllerWear.getInstance().mainActivity = null;
        Log.i(TAG, "onStop");
    }

    // Disconnect from the data layer when the Activity stops
    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (buttons == null) {
            buttons = new Button[]{
                    (Button) view.findViewById(R.id.btn1),
                    (Button) view.findViewById(R.id.btn2),
                    (Button) view.findViewById(R.id.btn3),
                    (Button) view.findViewById(R.id.btn4)
            };
            for (Button b : buttons) {
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(v.getTag() instanceof Boolean)) v.setTag(false);

                        Boolean val = (Boolean) v.getTag();
                        if (val) {
                            v.setBackgroundResource(R.drawable.bulb_off);
                        } else {
                            v.setBackgroundResource(R.drawable.bulb_on);
                        }
                        v.setTag(!val);

                        ControllerWear.getInstance().sendClickToMobile(((Button) v).getText().toString());
                    }
                });
            }
        }
        ControllerWear.getInstance().mainActivity = this;
        Log.i(TAG, "onStart");
    }
}
