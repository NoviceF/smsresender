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
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SMSTestActivity extends Activity {

    final String LOG_TAG = "sms.test.SMSTestActivity";

    final int TASK1_CODE = 1;
    final int TASK2_CODE = 2;
    final int TASK3_CODE = 3;

    public final static int STATUS_START = 100;
    public final static int STATUS_FINISH = 200;

    public final static String PARAM_TIME = "time";
    public final static String PARAM_TASK = "task";
    public final static String PARAM_RESULT = "result";
    public final static String PARAM_STATUS = "status";

    public final static String BROADCAST_ACTION = "com.birkettconsulting.sms.test";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    	setMessageIfNotNull(getMessageFromIntent(getIntent()));
        startService();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Runing smsservice!", Toast.LENGTH_SHORT);
        toast.show();
        finish();
    }

    void startService() {
        // Создаем Intent для вызова сервиса,
        // кладем туда параметр времени и код задачи
        Intent intent = new Intent(this, SMSService.class);
        // стартуем сервис
        startService(intent);
    }
}