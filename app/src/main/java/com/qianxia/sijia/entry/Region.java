package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2016/10/11.
 */
public class Region extends BmobObject {
    private String name;
    private City city;
    private List<String> neighborhoods;

    public Region() {
    }

    public Region(City city, String name, List<String> neighborhoods) {
        this.city = city;
        this.name = name;
        this.neighborhoods = neighborhoods;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNeighborhoods() {
        return neighborhoods;
    }

    public void setNeighborhoods(List<String> neighborhoods) {
        this.neighborhoods = neighborhoods;
    }

    @Override
    public String toString() {
        return "Region{" +
                "city=" + city +
                ", name='" + name + '\'' +
                ", neighborhoods=" + neighborhoods +
                '}';
    }
}
