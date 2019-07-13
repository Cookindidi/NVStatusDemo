package com.reeman.nvstatusdemo.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.reeman.nvstatusdemo.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GJ on 2019/6/11
 */
public class FileUtils {

    /**
     *
     * @return
     */
    public static String[] getLocationCfg() {
        String location = readFileFromSDCard(Constants.nav_coordinate, "locations.cfg");
        String[] item = location.split(";");
        if (item.length != 0) {
            Log.d("ggg", "加载导航位置信息完成");
            return item;
        }
        Log.d("ggg", "加载导航位置信息出错，文件格式错误");
        return item;
    }

    /**
     *
     * @param path
     * @param fileName
     * @return
     */
    public static String readFileFromSDCard(String path, String fileName) {
        File file = new File(getSDPath() + path + fileName);
        if (!file.exists()) {
            return "";
        } else {
            String text = "";
            FileInputStream fileInputStream = null;
            ByteArrayOutputStream outStream = null;

            try {
                fileInputStream = new FileInputStream(file);
                outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while((len = fileInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                byte[] content_byte = outStream.toByteArray();
                text = new String(content_byte, "UTF-8");
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }

                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException var16) {
                    var16.printStackTrace();
                }
            }
            return text;
        }
    }

    public static String getSDPath() {
        String sdPath = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();
            if (sdDir != null) {
                sdPath = sdDir.toString();
            }
        }

        return sdPath;
    }
}
