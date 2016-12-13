package com.qianxia.sijia.entry;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SearchResult {
    private String foodId;
    private String shopId;
    private String name;

    @Override
    public String toString() {
        return "SearchResult{" +
                "foodId='" + foodId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
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
}
