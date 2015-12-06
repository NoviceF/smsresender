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
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SMSTestActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//    	setMessageIfNotNull(getMessageFromIntent(getIntent()));

        TextView isHavePhoneTW = (TextView)findViewById(R.id.is_have_phone);

        if (isHavePhoneTW.length() == 0) {

        }
//        myAwesomeTextView.setText("My Awesome Text");
//        PhoneNumberUtils.isGlobalPhoneNumber("+912012185234"));



//        startService();
//        finish();
    }

    void startService() {
        // Создаем Intent для вызова сервиса,
        // кладем туда параметр времени и код задачи
        Intent intent = new Intent(this, SMSService.class);
        // стартуем сервис
        startService(intent);

        Toast toast = Toast.makeText(getApplicationContext(),
                "Runing smsservice!", Toast.LENGTH_SHORT);
        toast.show();
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
}