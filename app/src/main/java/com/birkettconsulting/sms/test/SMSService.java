package com.birkettconsulting.sms.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSService extends Service {

    final String LOG_TAG = "sms.test.SMSService";

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
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MyService onStartCommand");



        return super.onStartCommand(intent, flags, startId);
    }

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
            Intent intent = new Intent(SMSTestActivity.BROADCAST_ACTION);
            Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
            try {
                // сообщаем об старте задачи
                intent.putExtra(SMSTestActivity.PARAM_TASK, task);
                intent.putExtra(SMSTestActivity.PARAM_STATUS, SMSTestActivity.STATUS_START);
                sendBroadcast(intent);

                // начинаем выполнение задачи
                TimeUnit.SECONDS.sleep(time);

                // сообщаем об окончании задачи
                intent.putExtra(SMSTestActivity.PARAM_STATUS, SMSTestActivity.STATUS_FINISH);
                intent.putExtra(SMSTestActivity.PARAM_RESULT, time * 100);
                sendBroadcast(intent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop();
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
                }
            }
        }
    };
}