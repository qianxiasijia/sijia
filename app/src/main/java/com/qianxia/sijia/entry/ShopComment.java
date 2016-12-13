package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/10/4.
 */
public class ShopComment extends BmobObject {

    private Shop shop;
    private String pics;
    private String content;
    private SijiaUser author;
    private long time;

    @Override
    public String toString() {
        return "ShopComment{" +
                "author=" + author +
                ", shop=" + shop +
                ", pics='" + pics + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }

    public SijiaUser getAuthor() {
        return author;
    }

    public void setAuthor(SijiaUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
