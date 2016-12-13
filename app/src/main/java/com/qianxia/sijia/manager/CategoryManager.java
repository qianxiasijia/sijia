package com.qianxia.sijia.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.entry.MainCategory;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.entry.SubCategory;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.listener.OnMainCategoriesLoadedListener;
import com.qianxia.sijia.listener.OnSubCategoriesLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/11/6.
 */
public class CategoryManager {

    public static void getMainCategories(final Context context, final OnMainCategoriesLoadedListener listener) {
        final DBUtil dbUtil = new DBUtil(context);
        List<MainCategoryTable> list = dbUtil.getMainCategories();
        if (list != null && list.size() > 0) {
            listener.onMainCategoriesLoaded(list);
            return;
        }
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<MainCategory> query = new BmobQuery<>();
        query.findObjects(context, new FindListener<MainCategory>() {
            @Override
            public void onSuccess(List<MainCategory> list) {
                if (list != null && list.size() > 0) {
                    final List<MainCategoryTable> mainCategoryTables = new ArrayList<MainCategoryTable>();
                    for (final MainCategory mainCategory : list) {
                        MainCategoryTable mainCategoryTable = new MainCategoryTable();
                        mainCategoryTable.setId(mainCategory.getObjectId());
                        mainCategoryTable.setName(mainCategory.getCategoryName());
                        mainCategoryTable.setImgUrl(mainCategory.getPic().getUrl());
                        mainCategoryTables.add(mainCategoryTable);
                    }
                    listener.onMainCategoriesLoaded(mainCategoryTables);
                    dbUtil.saveMainCategories(mainCategoryTables);
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, i + ":" + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void getSubCategories(final Context context, final String mainCategoryId, final OnSubCategoriesLoadedListener listener) {
        final DBUtil dbUtil = new DBUtil(context);
        List<SubCategoryTable> list = dbUtil.getSubCategories(mainCategoryId);
        if (list != null && list.size() > 0) {
            listener.onSubCategoriesLoaded(list);
            return;
        }
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络信号不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        MainCategory mainCategory = new MainCategory();
        mainCategory.setObjectId(mainCategoryId);
        BmobQuery<SubCategory> query = new BmobQuery<>();
        query.addWhereEqualTo("mainCategory", mainCategory);
        query.findObjects(context, new FindListener<SubCategory>() {
            @Override
            public void onSuccess(List<SubCategory> list) {
                if (list != null && list.size() > 0) {
                    List<SubCategoryTable> subCategoryTablelist = new ArrayList<>();
                    for (SubCategory subCategory : list) {
                        SubCategoryTable subCategoryTable = new SubCategoryTable();
                        subCategoryTable.setId(subCategory.getObjectId());
                        subCategoryTable.setName(subCategory.getCategoryName());
                        subCategoryTable.setImgUrl(subCategory.getPic().getUrl());
                        subCategoryTable.setMainCategoryId(mainCategoryId);
                        subCategoryTablelist.add(subCategoryTable);
                    }
                    dbUtil.saveSubCategories(subCategoryTablelist);
                    listener.onSubCategoriesLoaded(subCategoryTablelist);
                } else {
                    Toast.makeText(context, "网络信号不良，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络信号不良，请稍后重试！", Toast.LENGTH_SHORT).show();
//                Log.i("TAG:getSubCategories",i+":"+s);
            }
        });

    }
}
