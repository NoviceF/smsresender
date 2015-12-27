package com.no.sms.resender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by novice on 27.12.15.
 */
public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, CustomSMSService.class);
            intent.putExtra(CustomSMSService.SERVICE_RUNNING_TYPE_TAG, CustomSMSService.SERVICE_RUN_SILENT);
            context.startService(serviceIntent);
        }
    }
}