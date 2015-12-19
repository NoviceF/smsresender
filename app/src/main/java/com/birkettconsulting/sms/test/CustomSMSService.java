package com.birkettconsulting.sms.test;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class CustomSMSService extends Service {

    final String LOG_TAG = "CustomSMSService";
    
    public static final String SERVICE_STATUS_SIGN = "service_status";
    public static final String SERVICE_STATUS_UP = "service_UP";
    public static final String SERVICE_STATUS_DOWN = "service_DOWN";

    private String targetNumber;

    public void onCreate() {
        super.onCreate();

        createBroadcastReceiver();

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

    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void createBroadcastReceiver(){
        Log.d(LOG_TAG, "MyService createBroadcastReceiver");
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
                String from = smsMessage.getOriginatingAddress();
                String content = smsMessage.getDisplayMessageBody();

                if (from != null && from.length() > 0 && content != null && content.length() > 0) {
                    processMessage(from, content);
                }
            }
        }

        void processMessage(String from, String content) {

            assert(from != null && content != null);

            Log.d(LOG_TAG, "MyService receive sms from = " + from);
            Log.d(LOG_TAG, "MyService receive sms content = " + content);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(targetNumber, null, "!auto! " +content, null, null);

        }
    };
}