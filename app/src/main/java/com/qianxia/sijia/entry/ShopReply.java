package com.qianxia.sijia.entry;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/10/4.
 */
public class ShopReply extends BmobObject {
    private ShopComment shopComment;
    private String content;
    private SijiaUser author;
    private long time;
    private String title;

    @Override
    public String toString() {
        return "ShopReply{" +
                "author=" + author +
                ", shopComment=" + shopComment +
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

    public ShopComment getShopComment() {
        return shopComment;
    }

    public void setShopComment(ShopComment shopComment) {
        this.shopComment = shopComment;
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
