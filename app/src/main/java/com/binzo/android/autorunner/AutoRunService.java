package com.binzo.android.autorunner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author Binzo
 */
public class AutoRunService extends Service {

    public static final String TAG = "AutoRunService";
    private AutoRunnerThread runnerThread = null;
    private ScreenStateReceiver mScreenStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        runnerThread = new AutoRunnerThread();
        runnerThread.start();
        LogX.logDebug(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerSreenStateReceiver();
        LogX.logDebug(TAG, "onStartCommand()");
        acquireWakeLock();
        startTicker();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogX.logDebug(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogX.logDebug(TAG, "onDestroy()");
        if (null != mScreenStateReceiver) {
            unregisterReceiver(mScreenStateReceiver);
        }
        if (null != mAlarmManager && null != mPendingIntent) {
            mAlarmManager.cancel(mPendingIntent);
        }
        runnerThread.setLooping(false);
    }

    private PowerManager.WakeLock wakeLock = null;
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        }
        if (null != wakeLock) {
            LogX.logDebug(TAG, "acquire WakeLock for 1s");
            wakeLock.acquire(1000);
        }
    }

    private PendingIntent mPendingIntent;
    private AlarmManager mAlarmManager;
    private void startTicker() {
        if (mPendingIntent == null) {
            Intent i = new Intent(this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        }
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
        mAlarmManager.cancel(mPendingIntent);

        int loopTimeInSec = 20; // 20 秒后唤醒
        long trigAtTime = SystemClock.elapsedRealtime() + loopTimeInSec * 1000;
        LogX.logDebug(TAG, "set alarm to wakeup after " + loopTimeInSec + "s");
        mAlarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                trigAtTime, mPendingIntent);  // 20秒生效，30秒不生效
    }

    private void registerSreenStateReceiver() {
        if (mScreenStateReceiver == null) {
            mScreenStateReceiver = new ScreenStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(mScreenStateReceiver, filter);
        }
    }
}
