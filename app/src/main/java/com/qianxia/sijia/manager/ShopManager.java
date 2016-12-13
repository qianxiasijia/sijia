package com.qianxia.sijia.manager;

import android.content.Context;
import android.widget.Toast;

import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.util.NetUtil;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by tarena on 2016/9/14.
 */
public class ShopManager {

    public static void getShopsBycity(Context context, CityNameBean cityNameBean, FindListener<Shop> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        City city = new City();
        city.setObjectId(cityNameBean.getCityId());
        BmobQuery<Shop> query = new BmobQuery<>();
        query.addWhereEqualTo("city", city);
        query.include("city,author");
        query.setLimit(1000);
        query.findObjects(context, listener);

    }

    public static void getShopById(Context context, String shopId, FindListener<Shop> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Shop> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", shopId);
        query.include("author");
        query.findObjects(context, listener);
    }

    public static void getShopByAuthorAndTime(Context context, String time, SijiaUser user, FindListener<Shop> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Shop> query = new BmobQuery<>();
        query.addWhereEqualTo("author", BmobUserManager.getInstance(context).getCurrentUser(SijiaUser.class));
        query.addWhereEqualTo("time", time);
        query.include("author");
        query.findObjects(context, listener);
    }
}
