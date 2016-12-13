package com.qianxia.sijia.entry;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/22.
 */
public class Collect extends BmobObject {
    private String foodId;
    private String shopId;
    private Long time;
    private String userId;
    private String content;
    private String imgUrl;
    private String name;

    @Override
    public String toString() {
        return "Collect{" +
                "content='" + content + '\'' +
                ", foodId='" + foodId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", time=" + time +
                ", userId='" + userId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
