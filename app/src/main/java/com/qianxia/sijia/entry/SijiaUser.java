package com.qianxia.sijia.entry;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by tarena on 2016/9/18.
 */
public class SijiaUser extends BmobChatUser {

    private String signature;
    private String region;
    private boolean gender;
    private String birthdate;
    private BmobGeoPoint location;
    private String pinYinName;
    private Character letter;
    private boolean allowFinded;
    private String city;

    @Override
    public String toString() {
        return "SijiaUser{" +
                "signature='" + signature + '\'' +
                ", region='" + region + '\'' +
                ", gender=" + gender +
                ", birthdate='" + birthdate + '\'' +
                ", location=" + location +
                ", pinYinName='" + pinYinName + '\'' +
                ", letter=" + letter +
                ", allowFinded=" + allowFinded +
                ", city='" + city + '\'' +
                '}';
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public String getPinYinName() {
        return pinYinName;
    }

    public void setPinYinName(String pinYinName) {
        this.pinYinName = pinYinName;
    }

    public Character getLetter() {
        return letter;
    }

    public void setLetter(Character letter) {
        this.letter = letter;
    }

    public boolean isAllowFinded() {
        return allowFinded;
    }

    public void setAllowFinded(boolean allowFinded) {
        this.allowFinded = allowFinded;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
