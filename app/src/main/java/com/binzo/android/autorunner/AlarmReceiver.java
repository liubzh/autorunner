package com.binzo.android.autorunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by binzo on 2017/12/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogX.logDebug(TAG, "onReceive");
        Intent i = new Intent(context, AutoRunService.class);
        context.startService(i);
    }
}