package com.binzo.android.autorunner;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * Created by binzo on 2017/12/27.
 */

public class FileUtils {

    public static void writeToFile(String content, String filePath) {
        ByteArrayInputStream bis = null;
        FileOutputStream fos = null;
        int buffSize = 1024;

        try {
            bis = new ByteArrayInputStream(content.getBytes());
            fos = new FileOutputStream(filePath);
            int byteCount = 0;
            byte[] buffer = new byte[buffSize];
            while((byteCount = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 配置文件是以键值对形式保存的，比如 STATUS=NOTHING_TO_DO，通过 '=' 来分割，获取某个配置项的值
     * @param key 配置项名字
     * @param filePath 读取文件的路径
     * @return
     */
    public static String readValueFromFile(String key, String filePath) {
        FileReader fr = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null) {
                if (null != key) {
                    if (line.startsWith(key + "=")) {
                        return line.substring(line.indexOf("=") + 1);
                    }
                } else {
                    if (null == sb) {
                        sb = new StringBuffer();
                        sb.append(line);
                    } else {
                        sb.append("\n").append(line);
                    }
                }
            }
            if (sb == null) {
                return null;
            } else {
                return sb.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
