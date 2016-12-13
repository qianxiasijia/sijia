package com.qianxia.sijia.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/11/6.
 */
@DatabaseTable
public class SijiaBitmap {
    @DatabaseField(id = true)
    private String imgUrl;
    @DatabaseField
    private String bitmap; //BASE64格式的字符串


    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "SijiaBitmap{" +
                "bitmap='" + bitmap + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
