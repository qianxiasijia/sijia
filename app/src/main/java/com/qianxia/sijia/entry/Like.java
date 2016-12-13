package com.qianxia.sijia.entry;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/13.
 */
public class Like extends BmobObject {

    private String likerId;
    private String foodId;
    private String shopId;

    @Override
    public String toString() {
        return "Like{" +
                "foodId='" + foodId + '\'' +
                ", likerId='" + likerId + '\'' +
                ", shopId='" + shopId + '\'' +
                '}';
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getLikerId() {
        return likerId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
