package com.qianxia.sijia.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.SearchResult;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.listener.OnFindResultListener;
import com.qianxia.sijia.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SearchManager {
    public static void getShopResult(final Context context, final String keyword, final OnFindResultListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<Shop> query = new BmobQuery<>();
        City city = new City();
        city.setObjectId(SijiaApplication.selectedCity.getCityId());
        query.addWhereEqualTo("city", city);
        query.findObjects(context, new FindListener<Shop>() {
            @Override
            public void onSuccess(List<Shop> list) {
                if (list != null && list.size() > 0) {
                    List<SearchResult> results = new ArrayList<SearchResult>();
                    for (Shop shop : list) {
                        String name = shop.getName();
                        if (name.contains(keyword)) {
                            Log.i("DEBUG", "getShopResult:" + name);
                            SearchResult result = new SearchResult();
                            result.setName(name);
                            result.setShopId(shop.getObjectId());
                            results.add(result);
                        }
                    }
                    listener.findResult(results);
                } else {
                    Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                Log.i("DEBUG", "i:s");
            }
        });
    }

    public static void getFoodResult(final Context context, final String keyword, final OnFindResultListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Food> query = new BmobQuery<>();
        City city = new City();
        city.setObjectId(SijiaApplication.selectedCity.getCityId());
        query.addWhereEqualTo("city", city);
        query.findObjects(context, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
                    List<SearchResult> results = new ArrayList<SearchResult>();
                    for (Food food : list) {
                        String name = food.getName();
                        if (name.contains(keyword)) {
                            SearchResult result = new SearchResult();
                            result.setFoodId(food.getObjectId());
                            result.setName(name);
                            results.add(result);
                        }
                    }
                    listener.findResult(results);
                } else {
                    Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                Log.i("DEBUG", "i:s");
            }
        });
    }
}
