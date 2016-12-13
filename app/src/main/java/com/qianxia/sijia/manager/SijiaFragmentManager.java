package com.qianxia.sijia.manager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.qianxia.sijia.fragment.CountFragment;
import com.qianxia.sijia.fragment.FoodFragment;
import com.qianxia.sijia.fragment.LoginFragment;
import com.qianxia.sijia.fragment.NearbyFragment;
import com.qianxia.sijia.fragment.ShareFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;

/**
 * Created by tarena on 2016/9/9.
 */
public class SijiaFragmentManager {


    public static List<Fragment> getFragmentList(Context context) {
        List<Fragment> fragmentList = new ArrayList<>();
        BmobUserManager manager = BmobUserManager.getInstance(context);
        fragmentList.add(new FoodFragment());
        fragmentList.add(new ShareFragment());
        fragmentList.add(new NearbyFragment());

        if (manager.getCurrentUser() != null) {
            fragmentList.add(new CountFragment());
        } else {
            fragmentList.add(new LoginFragment());
        }
        return fragmentList;
    }


}
