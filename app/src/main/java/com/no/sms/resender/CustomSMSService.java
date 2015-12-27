package com.no.sms.resender;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

    public static final String SERVICE_RUNNING_TYPE_TAG = "service_run_type";
    public static final String SERVICE_RUN_REGULAR = "";
    public static final String SERVICE_RUN_SILENT = "silent";

    private static final int NOTIFICATION_ID = 1;
    private static final String SERVICE_NOTIFICATION_NAME = "SMS resender service";

    private String targetNumber = "+";

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

//        String runType = getMessageFromIntent(intent);

//        if (runType.compareTo(SERVICE_RUN_REGULAR) == 0) {
////            startMainActivity(this, SERVICE_STATUS_UP);
//        } else if (runType.compareTo(SERVICE_RUN_SILENT) == 0) {
////            startMainActivity(this, SERVICE_RUN_SILENT);
//        }

        startMainActivity(this, SERVICE_STATUS_UP);

//        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(R.drawable.ic_launcher,
                SERVICE_NOTIFICATION_NAME, System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                getActivityIntent(this, "msg for pending intent"), PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setLatestEventInfo(this, SERVICE_NOTIFICATION_NAME, SERVICE_STATUS_UP, contentIntent);

        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.notify(NOTIFICATION_ID, notification);

        startForeground(NOTIFICATION_ID, notification);

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
        Intent intent = new Intent(context, SMSResenderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(SERVICE_STATUS_SIGN, status);
        return intent;
    }

    private void startMainActivity(Context context, String message) {
        context.startActivity(getActivityIntent(context, message));
    }

    private String getMessageFromIntent(Intent intent) {
        return intent.getStringExtra(CustomSMSService.SERVICE_RUNNING_TYPE_TAG);
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

            if (content.contains("nalichnyh.")) {

                Log.d(LOG_TAG, "MyService receive sms from = " + from);
                Log.d(LOG_TAG, "MyService receive sms content = " + content);

//                SmsManager sms = SmsManager.getDefault();
//                sms.sendTextMessage(targetNumber, null, "!! " + content, null, null);

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("!! " + content, "");
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
            }
        }
    };
}
