package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.ShopComment;

import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public interface OnShopCommentsLoadedListener {
    void onShopCommentsLoaded(List<ShopComment> list);
}
