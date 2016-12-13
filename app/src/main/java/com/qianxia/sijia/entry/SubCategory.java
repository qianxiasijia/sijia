package com.qianxia.sijia.entry;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by tarena on 2016/9/9.
 */
public class SubCategory extends BmobObject {

    private BmobFile pic;
    private MainCategory mainCategory;
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public MainCategory getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(MainCategory mainCategory) {
        this.mainCategory = mainCategory;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }

    public SubCategory(String categoryName, MainCategory mainCategory, BmobFile pic) {
        this.categoryName = categoryName;
        this.mainCategory = mainCategory;
        this.pic = pic;
    }

    public SubCategory() {
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "categoryName='" + categoryName + '\'' +
                ", pic=" + pic +
                ", mainCategory=" + mainCategory +
                '}';
    }
}
