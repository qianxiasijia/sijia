package com.qianxia.sijia.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/11/6.
 */
@DatabaseTable
public class MainCategoryTable {
    @DatabaseField
    private String name;
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String imgUrl;

    public MainCategoryTable() {
    }

    @Override
    public String toString() {
        return "MainCategoryTable{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
