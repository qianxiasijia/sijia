package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.CityNameBean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 */
public interface OnLoadedAllCitiesListener {
    void onLoadedAllCities(List<CityNameBean> list);
}
