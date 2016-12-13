package com.qianxia.sijia.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qianxia.sijia.util.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tarena on 2016/9/18.
 */
public class LoginHttpManager {
    public static String SESSIONID = "";

    public static Bitmap getVerifyCodeHttp(String path) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                SESSIONID = connection.getHeaderField("Set-Cookie").split(";")[0];
                InputStream in = connection.getInputStream();
                byte[] datas = null;
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                datas = out.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static boolean loginHttp(String userName, String password, String code, String path) {
        boolean flag = false;
        Map<String, String> params = new HashMap<>();
        params.put("userName", userName);
        params.put("password", password);
        params.put("code", code);
        StringBuilder loginBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            loginBuilder.append(entry.getKey());
            loginBuilder.append("=");
            loginBuilder.append(entry.getValue());
            loginBuilder.append("&");
        }
        String loginStr = loginBuilder.deleteCharAt(loginBuilder.length() - 1).toString();
        byte[] datas = loginStr.getBytes();
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Cookie", SESSIONID);
            connection.setRequestProperty("Content-Length", String.valueOf(datas.length));

            OutputStream out = connection.getOutputStream();
            out.write(datas);
            out.flush();
            out.close();

            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                InputStream in = connection.getInputStream();
                String jsonStr = StreamUtils.getJsonStrByInputStream(in);
                JSONObject jsonObject = new JSONObject(jsonStr);
                String result = jsonObject.getString("result");
                if ("ok".equals(result)) {
                    flag = true;
                } else {
                    Log.i("TAG", jsonObject.getString("msg"));
                }
            } else {
                Log.i("TAG", String.valueOf(statusCode));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flag;
    }

}
