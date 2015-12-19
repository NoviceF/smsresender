package com.birkettconsulting.sms.test;

import java.util.Date;
import java.util.Calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class CustomSMSService extends Service {

    final String LOG_TAG = "CustomSMSService";
    final String PREFS_NAME = LOG_TAG;
    final String DATE_SIGN = "date";
    final String TEXT_SIGN = "text";
    public static final String SERVICE_STATUS_SIGN = "service_status";
    public static final String SERVICE_STATUS_UP = "service_UP";
    public static final String SERVICE_STATUS_DOWN = "service_DOWN";

    String targetNumber;

    Runnable run = new Runnable() {

        @Override
        public void run() {
//            Log.d(LOG_TAG, "MyService send sms");
        }
    };

    public void onCreate() {
        super.onCreate();

        createBR();

        Log.d(LOG_TAG, "MyService onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d(LOG_TAG, "MyService onDestroy");

        startMainActivity(this, SERVICE_STATUS_DOWN);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyService onStartCommand");
        startMainActivity(this, SERVICE_STATUS_UP);
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        setMessageIfNotNull(getMessageFromIntent(intent));
//    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    class MyRun implements Runnable {

        int time;
        int startId;
        int task;

        public MyRun(int startId, int time, int task) {
            this.time = time;
            this.startId = startId;
            this.task = task;
            Log.d(LOG_TAG, "MyRun#" + startId + " create");
        }

        public void run() {
//            Intent intent = new Intent(SMSTestActivity.BROADCAST_ACTION);
//            Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
//            try {
//                // сообщаем об старте задачи
//                intent.putExtra(SMSTestActivity.PARAM_TASK, task);
//                intent.putExtra(SMSTestActivity.PARAM_STATUS, SMSTestActivity.STATUS_START);
//                sendBroadcast(intent);
//
//                // начинаем выполнение задачи
//                TimeUnit.SECONDS.sleep(time);
//
//                // сообщаем об окончании задачи
//                intent.putExtra(SMSTestActivity.PARAM_STATUS, SMSTestActivity.STATUS_FINISH);
//                intent.putExtra(SMSTestActivity.PARAM_RESULT, time * 100);
//                sendBroadcast(intent);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            stop();
        }

        void stop() {
            Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
                    + startId + ") = " + stopSelfResult(startId));
        }
    }

    private void createBR(){
        Log.d(LOG_TAG, "MyService onCreateBR");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(receiver, filter);
    }

    private SmsMessage getMessageBody(Intent intent) {
        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[0]);
        return message;
    }

    private static Intent getActivityIntent(Context context, String status) {
        Intent intent = new Intent(context, SMSTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(SERVICE_STATUS_SIGN, status);
        return intent;
    }

    private void startMainActivity(Context context, String message) {
        context.startActivity(getActivityIntent(context, message));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                //action for sms received
                SmsMessage smsMessage = getMessageBody(intent);
                String smsBody = smsMessage.getDisplayMessageBody();
                if (smsBody != null) {
                    Log.d(LOG_TAG, "MyService receive sms = " + smsBody);

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(targetNumber, null, smsBody, null, null);
                }
            }
        }

        public void updatePreference(String messageContent) {
            // Get from the SharedPreferences
            SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
            String dateStr = settings.getString(DATE_SIGN, "");

            if (dateStr.length() == 0) {
                makeNewRecord(messageContent);
            }

            // need to compare last record date and current, if current newer - save current instead
        }

        public void makeNewRecord(String messageContent) {
            Date dt = Calendar.getInstance().getTime();
            String dateString = dt.toGMTString();
            SharedPreferences settings = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(DATE_SIGN, dateString);
            editor.putString(TEXT_SIGN, messageContent);
            // Apply the edits!
            editor.commit();
        }
    };
}