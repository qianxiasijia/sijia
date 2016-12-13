package com.qianxia.sijia.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by tarena on 2016/10/17.
 */

@DatabaseTable
public class CityNameBean {
    @DatabaseField(id = true)
    private String cityId;
    @DatabaseField
    private String cityName;
    @DatabaseField
    private String pyName;
    @DatabaseField
    private char letter;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public String getPyName() {
        return pyName;
    }

    public void setPyName(String pyName) {
        this.pyName = pyName;
    }

    @Override
    public String toString() {
        return "CityNameBean{" +
                "cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", pyName='" + pyName + '\'' +
                ", letter=" + letter +
                '}';
    }

}
