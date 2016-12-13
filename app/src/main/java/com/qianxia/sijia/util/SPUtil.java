package com.qianxia.sijia.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;

import cn.bmob.im.BmobUserManager;

/**
 * Created by tarena on 2016/10/17.
 */
public class SPUtil {

    private SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    public SPUtil(String name) {
        sp = SijiaApplication.context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public SPUtil(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sp.edit();
    }

    public boolean isFirstUse() {
        return sp.getBoolean(Constant.IS_FIRST_USE, true);
    }

    public void setFirstLogin(boolean flag) {
        editor.putBoolean(Constant.IS_FIRST_USE, flag);
        editor.commit();
    }

    public boolean isLogin() {
        return sp.getBoolean(Constant.IS_LOGIN, false);
    }

    public void setLogin(boolean flag) {
        editor.putBoolean(Constant.IS_LOGIN, flag);
        editor.commit();
    }

    public boolean isAllowFinded() {
        return sp.getBoolean(Constant.ALLOW_FINDED, true);
    }

    public void setAllowFinded(boolean flag) {
        editor.putBoolean(Constant.ALLOW_FINDED, flag);
        editor.commit();
    }

    public boolean isAllowNotify() {
        return sp.getBoolean(Constant.ALLOW_NOTIFICATION, true);
    }

    public void setAllowNotify(boolean flag) {
        editor.putBoolean(Constant.ALLOW_NOTIFICATION, flag);
        editor.commit();
    }

    public boolean isAllowShake() {
        return sp.getBoolean(Constant.ALLOW_SHAKE, true);
    }

    public void setAllowShake(boolean flag) {
        editor.putBoolean(Constant.ALLOW_SHAKE, flag);
        editor.commit();
    }

    public boolean isAllowVoice() {
        return sp.getBoolean(Constant.ALLOW_VOICE, true);
    }

    public void setAllowVoice(boolean flag) {
        editor.putBoolean(Constant.ALLOW_VOICE, flag);
        editor.commit();
    }

    public String getSelectedCity() {
        return sp.getString("city", "北京");
    }

    public void setSelectedCity(String city) {
        editor.putString("city", city);
        editor.commit();
    }

}
