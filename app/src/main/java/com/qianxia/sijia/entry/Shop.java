package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2016/8/21.
 */
public class Shop extends BmobObject {

    private String name;
    private Float rating;
    private List<BmobFile> pics;
    private Integer likerNum;
    private String description;
    private BmobRelation collector;
    private City city;
    private String address;
    private String openTime;
    private BmobGeoPoint locationPoint;
    private Integer commentNum;
    private String street;
    private SijiaUser author;
    private String time;
    private String telephone;

    @Override
    public String toString() {
        return "Shop{" +
                "name='" + name + '\'' +
                ", rating=" + rating +
                ", pics=" + pics +
                ", likerNum=" + likerNum +
                ", description='" + description + '\'' +
                ", collector=" + collector +
                ", city=" + city +
                ", address='" + address + '\'' +
                ", openTime='" + openTime + '\'' +
                ", locationPoint=" + locationPoint +
                ", commentNum=" + commentNum +
                ", street='" + street + '\'' +
                ", author=" + author +
                ", time='" + time + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public List<BmobFile> getPics() {
        return pics;
    }

    public void setPics(List<BmobFile> pics) {
        this.pics = pics;
    }

    public Integer getLikerNum() {
        return likerNum;
    }

    public void setLikerNum(Integer likerNum) {
        this.likerNum = likerNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BmobRelation getCollector() {
        return collector;
    }

    public void setCollector(BmobRelation collector) {
        this.collector = collector;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public BmobGeoPoint getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(BmobGeoPoint locationPoint) {
        this.locationPoint = locationPoint;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public SijiaUser getAuthor() {
        return author;
    }

    public void setAuthor(SijiaUser author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
