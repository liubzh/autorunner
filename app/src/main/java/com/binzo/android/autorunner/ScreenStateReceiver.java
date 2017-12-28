package com.binzo.android.autorunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by binzo on 2017/12/27.
 */

public class ScreenStateReceiver extends BroadcastReceiver {

    public static final String TAG = "ScreenStateReceiver";
    public String SCREEN_STATE_FILE = "/data/local/tmp/autorun.screen";

    private final String SCREEN_ON = Intent.ACTION_SCREEN_ON;
    private final String SCREEN_OFF = Intent.ACTION_SCREEN_OFF;

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = "";
        if (SCREEN_ON.equals(intent.getAction())) {
            state = "SCREEN_STATE=ON\n";
        } else if (SCREEN_OFF.equals(intent.getAction())) {
            state = "SCREEN_STATE=OFF\n";
        }
        LogX.logDebug(TAG, state);
        FileUtils.writeToFile(state, SCREEN_STATE_FILE);
    }

}
