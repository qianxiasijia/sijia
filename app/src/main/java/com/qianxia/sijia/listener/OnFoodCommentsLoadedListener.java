package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.FoodComment;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */
public interface OnFoodCommentsLoadedListener {
    void onFoodCommentsLoaded(List<FoodComment> foodComments);
}
