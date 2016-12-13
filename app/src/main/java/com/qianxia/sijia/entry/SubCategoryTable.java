package com.qianxia.sijia.entry;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2016/11/7.
 */
@DatabaseTable
public class SubCategoryTable implements Parcelable {
    @DatabaseField
    private String name;
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private String mainCategoryId;
    @DatabaseField
    private String imgUrl;

    protected SubCategoryTable(Parcel in) {
        name = in.readString();
        id = in.readString();
        mainCategoryId = in.readString();
        imgUrl = in.readString();
    }

    public SubCategoryTable() {
    }

    public static final Creator<SubCategoryTable> CREATOR = new Creator<SubCategoryTable>() {
        @Override
        public SubCategoryTable createFromParcel(Parcel in) {
            return new SubCategoryTable(in);
        }

        @Override
        public SubCategoryTable[] newArray(int size) {
            return new SubCategoryTable[size];
        }
    };

    @Override
    public String toString() {
        return "SubCategoryTable{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mainCategoryId='" + mainCategoryId + '\'' +
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

    public String getMainCategoryId() {
        return mainCategoryId;
    }

    public void setMainCategoryId(String mainCategoryId) {
        this.mainCategoryId = mainCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(mainCategoryId);
        dest.writeString(imgUrl);
    }
}
