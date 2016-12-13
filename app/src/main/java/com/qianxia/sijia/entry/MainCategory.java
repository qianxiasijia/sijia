package com.qianxia.sijia.entry;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by tarena on 2016/9/9.
 */
public class MainCategory extends BmobObject {
    private BmobFile pic;
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public MainCategory(String categoryName, BmobFile pic) {
        this.categoryName = categoryName;
        this.pic = pic;
    }

    public MainCategory() {
    }

    @Override
    public String toString() {
        return "MainCategory{" +
                "categoryName='" + categoryName + '\'' +
                ", pic=" + pic +
                '}';
    }
}
