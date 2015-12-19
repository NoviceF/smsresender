package com.birkettconsulting.sms.test;

import android.app.Activity;
/**
 * 
 *  Copyright 2012 Birkett Consulting
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SMSTestActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initButtons();
        startSMSService();

//        updateServiceStatus(true);


//        if (isHavePhoneTW.length() == 0) {
//
//        }
//        myAwesomeTextView.setText("My Awesome Text");
//        PhoneNumberUtils.isGlobalPhoneNumber("+912012185234"));


//        finish();
    }

    private void initButtons() {
        TextView statusTextView = (TextView)findViewById(R.id.StatusMarqueeText);
        statusTextView.setTextColor(Color.WHITE);
        statusTextView.setBackgroundColor(Color.YELLOW);
        statusTextView.setText("Service unknown state");

        Button buttonStartService = (Button) findViewById(R.id.startButton);
        buttonStartService.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startSMSService();
            }
        });

        Button buttonStopService = (Button) findViewById(R.id.stopButton);
        buttonStopService.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                stopSMSService();
            }
        });
    }

    void startSMSService() {
        // Создаем Intent для вызова сервиса,
        // кладем туда параметр времени и код задачи
        Intent intent = new Intent(this, CustomSMSService.class);
        // стартуем сервис
        startService(intent);
    }

    void stopSMSService() {
        Intent intent = new Intent(this, CustomSMSService.class);
        stopService(intent);
    }

    String getTargetNumberFromPrefs() {
        final String perfsName = getResources().getString(R.string.prefs_name);
        final String phoneTage = getResources().getString(R.string.phone_tag);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(perfsName, 0);
        return settings.getString(phoneTage, "");
    }

    void setTargetNumberToPrefs(String phoneNumber) {
        final String perfsName = getResources().getString(R.string.prefs_name);
        final String phoneTage = getResources().getString(R.string.phone_tag);

        SharedPreferences settings = getApplicationContext().getSharedPreferences(perfsName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(phoneTage, phoneNumber);
        // Apply the edits!
        editor.commit();
    }

    void updateServiceStatus(boolean status) {

        TextView statusTextView = (TextView)findViewById(R.id.StatusMarqueeText);
        statusTextView.setTextColor(Color.WHITE);

        if (status) {
            statusTextView.setBackgroundColor(Color.GREEN);
            statusTextView.setText("Service running");
        } else {
            statusTextView.setBackgroundColor(Color.RED);
            statusTextView.setText("Service Stopped");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        updateServiceStatusFromIntent(getMessageFromIntent(intent));
    }

    private void updateServiceStatusFromIntent(String text) {
        assert(text != null);

        if (text != null) {
            if (text.compareTo(CustomSMSService.SERVICE_STATUS_UP) == 0) {
                Log.d("CustomMyActivity", "Activity update status to UP");
                updateServiceStatus(true);
            } else if (text.compareTo(CustomSMSService.SERVICE_STATUS_DOWN) == 0) {
                Log.d("CustomMyActivity", "Activity update status to DOWN");
                updateServiceStatus(false);
            }
        }
    }

    private String getMessageFromIntent(Intent intent) {
        return intent.getStringExtra(CustomSMSService.SERVICE_STATUS_SIGN);
    }
}