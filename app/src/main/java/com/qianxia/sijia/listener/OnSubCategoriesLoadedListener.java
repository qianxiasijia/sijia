package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.SubCategoryTable;

import java.util.List;

/**
 * Created by Administrator on 2016/11/7.
 */
public interface OnSubCategoriesLoadedListener {
    void onSubCategoriesLoaded(List<SubCategoryTable> list);
}
