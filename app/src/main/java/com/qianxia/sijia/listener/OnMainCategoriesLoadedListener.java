package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.MainCategoryTable;

import java.util.List;

/**
 * Created by Administrator on 2016/11/6.
 */
public interface OnMainCategoriesLoadedListener {
    void onMainCategoriesLoaded(List<MainCategoryTable> mainCategoryTables);
}
