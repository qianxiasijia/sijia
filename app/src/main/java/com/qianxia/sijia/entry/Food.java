package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2016/8/21.
 */
public class Food extends BmobObject {
    private SubCategory subCategory;
    private BmobRelation collector;
    private String description;
    private Integer likerNum;
    private Integer commentNum;
    private String name;
    private List<BmobFile> pics;
    private Float price;
    private Float rating;
    private Shop shop;
    private City city;
    private SijiaUser author;

    public Food() {
    }

    @Override
    public String toString() {
        return "Food{" +
                "author=" + author +
                ", subCategory=" + subCategory +
                ", collector=" + collector +
                ", description='" + description + '\'' +
                ", likerNum=" + likerNum +
                ", commentNum=" + commentNum +
                ", name='" + name + '\'' +
                ", pics=" + pics +
                ", price=" + price +
                ", rating=" + rating +
                ", shop=" + shop +
                ", city=" + city +
                '}';
    }

    public SijiaUser getAuthor() {
        return author;
    }

    public void setAuthor(SijiaUser author) {
        this.author = author;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public BmobRelation getCollector() {
        return collector;
    }

    public void setCollector(BmobRelation collector) {
        this.collector = collector;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLikerNum() {
        return likerNum;
    }

    public void setLikerNum(Integer likerNum) {
        this.likerNum = likerNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BmobFile> getPics() {
        return pics;
    }

    public void setPics(List<BmobFile> pics) {
        this.pics = pics;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }
}
