package com.binzo.android.autorunner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

/**
 * Created by binzo on 2017/12/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String CONFIG_FILE="/data/local/tmp/autorun.conf";
    private Button btWakeupService;
    private Button btShellService;
    private Button btStopAll;
    private Button btTest;
    private SharedPreferences preferences;
    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, AutoRunService.class);
        layout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout_update();
    }

    private void layout() {
        btWakeupService = (Button) findViewById(R.id.ButtonWakeupService);
        btWakeupService.setOnClickListener(this);
        btShellService = (Button) findViewById(R.id.ButtonShellService);
        btShellService.setOnClickListener(this);
        btStopAll = (Button) findViewById(R.id.ButtonStopAll);
        btStopAll.setOnClickListener(this);
        btTest = (Button) findViewById(R.id.ButtonTest);
        btTest.setOnClickListener(this);
    }

    private void layout_update() {
        boolean running = isServiceRunning(this, "com.binzo.android.autorunner.AutoRunService");
        btWakeupService.setText(running ? R.string.stop_wakeup_service : R.string.start_wakeup_service);

//        String status = readContentFromFile(CONFIG_FILE);
//        if ("AUTORUN_ON=true".equals(status)) {
//            btShellService.setText(R.string.stop_shell_service);
//        } else {
//            btShellService.setText(R.string.start_shell_service);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ButtonTest:
                break;
            case R.id.ButtonWakeupService:
                if (getString(R.string.start_wakeup_service).equals(btWakeupService.getText())) {
                    startService(serviceIntent);
                    FileUtils.writeToFile("AUTORUN_ON=true", CONFIG_FILE);
                    btWakeupService.setText(R.string.stop_wakeup_service);
                } else if (getString(R.string.stop_wakeup_service).equals(btWakeupService.getText())) {
                    stopService(serviceIntent);
                    FileUtils.writeToFile("AUTORUN_ON=false", CONFIG_FILE);
                    btWakeupService.setText(R.string.start_wakeup_service);
                }
                break;
            case R.id.ButtonShellService:
                if (getString(R.string.start_shell_service).equals(btShellService.getText())) {
                    //intent.setAction("MyNameIsApp");
                    //startService(serviceIntent);
                    btShellService.setText(R.string.stop_shell_service);
                } else if (getString(R.string.stop_shell_service).equals(btShellService.getText())) {

                    btShellService.setText(R.string.start_shell_service);
                }
                break;
            case R.id.ButtonStopAll:
                stopService(serviceIntent);
                FileUtils.writeToFile("AUTORUN_ON=exit", CONFIG_FILE);
                finish();
                break;
            default:
                break;
        }
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
