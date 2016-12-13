package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/10/4.
 */
public class FoodComment extends BmobObject {

    private String pics;
    private Food food;
    private String content;
    private SijiaUser author;
    private long time;

    @Override
    public String toString() {
        return "FoodComment{" +
                "author=" + author +
                ", pics='" + pics + '\'' +
                ", food=" + food +
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

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
