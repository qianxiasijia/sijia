package com.qianxia.sijia.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/12/1.
 */
public class CheckDataBaseUtil {

    public static void checkAndCreateDataBase(Context context) {
        String filePath = context.getFilesDir().getAbsolutePath();
        String dbPath = filePath.substring(0, filePath.lastIndexOf("/")) + "/databases/";
        String dbName = "sijia.db";
        File dbFile = new File(dbPath + dbName);
        if (!dbFile.exists()) {
            File file1 = new File(dbPath);
            if (!file1.exists()) {
                file1.mkdirs();
            }

            try {
                InputStream input = context.getAssets().open(dbName);
                FileOutputStream out = new FileOutputStream(dbFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                input.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
