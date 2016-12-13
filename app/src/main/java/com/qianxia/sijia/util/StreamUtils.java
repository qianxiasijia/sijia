package com.qianxia.sijia.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by tarena on 2016/9/18.
 */
public class StreamUtils {

    public static String getJsonStrByInputStream(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            StringBuilder resBuilder = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                resBuilder.append(line);
            }
            result = resBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }
}
