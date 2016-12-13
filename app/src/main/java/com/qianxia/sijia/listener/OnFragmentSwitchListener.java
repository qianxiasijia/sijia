package com.qianxia.sijia.listener;

import android.support.v4.app.Fragment;

import com.qianxia.sijia.constant.Constant;

/**
 * Created by tarena on 2016/9/18.
 */
public interface OnFragmentSwitchListener {

    void fragmentSwitch(Constant.EnumFragment fragment, Fragment newFragment);

    void refreshHeardView();
}
