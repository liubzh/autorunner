package com.binzo.android.autorunner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by binzo on 2017/12/22.
 */

public class AutoRunnerThread extends Thread {

    public static final String TAG = "AutoRunnerThread";
    private static final String SCREENSHOT_FILE = "/data/local/tmp/autorun_screenshot.png";
    private static final String COMMUNICATION_FILE = "/data/local/tmp/autorun_communication";
    private boolean looping = true;

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    @Override
    public void run() {
        try {
            while(looping) {
                if (new File(SCREENSHOT_FILE).exists() && new File(COMMUNICATION_FILE).exists()) {
                    String operation = FileUtils.readValueFromFile("OPERATION", COMMUNICATION_FILE);
                    LogX.logDebug(TAG, "operation: " + operation);
                    if ("NOTIFY_FRIENDS".equals(operation)) {
                        notifyFriends();
                    } else if ("SPEED_UP".equals(operation)) {
                        speed_up();
                    }
                }
                //LogX.logDebug(TAG, "looping");
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void speed_up() {
        StringBuffer sb = new StringBuffer();
        int color = getColor(SCREENSHOT_FILE, 387, 1748);
        if (color == 0xFC4145) {  // 红色卡片是加速卡
            sb.append("input tap 387 1748   # 点击加速卡\n");
        } else {
            sb.append("input tap 887 1748   # 点击加速卡\n");
        }
        sb.append("input tap 758 1246   # 点击使用按钮\n");
        sb.append("STATUS=GOT_COMMANDS\n");
        LogX.logDebug(TAG, "write to communication file: \n" + sb.toString());
        FileUtils.writeToFile(sb.toString(), COMMUNICATION_FILE);
    }

    private void notifyFriends() {
        StringBuffer sb = new StringBuffer();
        do {
            String commands = genNotifyFriendsCommands(SCREENSHOT_FILE);
            if (TextUtils.isEmpty(commands)) {
                LogX.logDebug(TAG, "nothing to do for the screenshot");
                sb.append("STATUS=NOTHING_TO_DO\n");
                break;
            }
            sb.append("STATUS=GOT_COMMANDS\n");
            sb.append(commands);
        } while(false);
        LogX.logDebug(TAG, "write to communication file: \n" + sb.toString());
        FileUtils.writeToFile(sb.toString(), COMMUNICATION_FILE);
    }

    /**
     * 获取某个坐标点的颜色值
     * @param imagePath  图片文件路径
     * @param x  横坐标值
     * @param y  纵坐标值
     * @return  颜色值，十进制数，RGB
     */
    private int getColor(String imagePath, int x, int y) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int clr = bitmap.getPixel(x, y);
        return (clr & 0x00ffffff);
    }

    private String genNotifyFriendsCommands(String imagePath) {
        int x = 967;
        int item_h = 193;
        StringBuffer sb = new StringBuffer();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int[] pixels = new int[1 * bitmap.getHeight()];//保存像素的数组，图片宽x高=1x1920
        bitmap.getPixels(pixels,0,1,x,0,1, bitmap.getHeight());
        for(int i = 700; i < pixels.length; i++){  // 纵坐标0-700的像素点忽略。
            int clr = pixels[i];
            if ((clr & 0x00ffffff) == 0) {
                sb.append("input tap " + x + " " + (i+1) + "  # 点击有偷吃图标的坐标\n");
                sb.append("sleep 3              # 等待网络加载\n");
                sb.append("input tap 400 1400   # 点击左边偷吃小鸡\n");
                sb.append("input tap 730 1300   # 点击发消息\n");
                sb.append("input keyevent 4     # 退出好友的蚂蚁庄园界面\n");
                sb.append("echo \"通知了一次好友\"\n");
                System.out.println("y:" + (i+1) + ", color:" + Integer.toHexString(clr));
                i = i + item_h;
            }
            //System.out.println(i + ": r="+red+",g="+green+",b="+blue);
        }
        return sb.toString();
    }
}
