package com.qianxia.sijia.entry;

import com.j256.ormlite.field.DatabaseField;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/17.
 */
public class City extends BmobObject {

    private String cityName;

    public City() {
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName + '\'' +
                '}';
    }

    public City(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
