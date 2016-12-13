package com.qianxia.sijia.manager;

import android.content.Context;

import com.qianxia.sijia.entry.FoodComment;
import com.qianxia.sijia.entry.FoodReply;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopComment;
import com.qianxia.sijia.entry.ShopReply;
import com.qianxia.sijia.listener.OnFoodReplayLoadedListener;
import com.qianxia.sijia.listener.OnShopReplayLoadedListener;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/11/12.
 */
public class ReplayManager {

    public static void getFoodReplays(Context context, FoodComment foodComment, final OnFoodReplayLoadedListener listener) {
        BmobQuery<FoodReply> query = new BmobQuery<>();
        query.addWhereEqualTo("foodComment", foodComment);
        query.include("author");
        query.order("createdAt");
        query.findObjects(context, new FindListener<FoodReply>() {
            @Override
            public void onSuccess(List<FoodReply> list) {
                if (list != null) {
                    listener.onFoodReplayLoaded(list);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    public static void getShopReplays(Context context, ShopComment shopComment, final OnShopReplayLoadedListener listener) {
        BmobQuery<ShopReply> query = new BmobQuery<>();
        query.addWhereEqualTo("shopComment", shopComment);
        query.order("createdAt");
        query.include("author");
        query.findObjects(context, new FindListener<ShopReply>() {
            @Override
            public void onSuccess(List<ShopReply> list) {
                if (list != null) {
                    listener.onShopReplayLoaded(list);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }
}
