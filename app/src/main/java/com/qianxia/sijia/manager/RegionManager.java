package com.qianxia.sijia.manager;

import android.content.Context;
import android.widget.Toast;

import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.Region;
import com.qianxia.sijia.listener.OnRegionLoadedListener;
import com.qianxia.sijia.util.NetUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/11/19.
 */
public class RegionManager {

    public static void getDistrictsByCity(final Context context, CityNameBean cityNameBean, final OnRegionLoadedListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Region> query = new BmobQuery<>();
        City city = new City();
        city.setObjectId(cityNameBean.getCityId());
        query.addWhereEqualTo("city", city);
        query.findObjects(context, new FindListener<Region>() {
            @Override
            public void onSuccess(List<Region> list) {
                if (list != null) {
                    listener.onRegionLoaded(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

