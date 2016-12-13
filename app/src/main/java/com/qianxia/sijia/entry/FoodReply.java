package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/10/4.
 */
public class FoodReply extends BmobObject {
    private FoodComment foodComment;
    private String content;
    private SijiaUser author;
    private long time;
    private String title;

    @Override
    public String toString() {
        return "FoodReply{" +
                "author=" + author +
                ", foodComment=" + foodComment +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
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

    public FoodComment getFoodComment() {
        return foodComment;
    }

    public void setFoodComment(FoodComment foodComment) {
        this.foodComment = foodComment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
