package com.qianxia.sijia.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.SubCategory;
import com.qianxia.sijia.util.NetUtil;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by tarena on 2016/9/14.
 */
public class FoodManager {
    public static void getFoodsByCityAndSubcategory(final Context context, CityNameBean cityNameBean, final String subCategoryId, final FindListener<Food> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        City city = new City();
        city.setObjectId(cityNameBean.getCityId());
        SubCategory subCategory = new SubCategory();
        subCategory.setObjectId(subCategoryId);
        BmobQuery<Food> queryFood = new BmobQuery<>();
        queryFood.addWhereEqualTo("city", city);
        queryFood.addWhereEqualTo("subCategory", subCategory);
        queryFood.include("author,shop.author");
        queryFood.order("-likerNum");
        queryFood.setLimit(10);
        queryFood.findObjects(context, listener);
//
//        BmobQuery<City> queryCity = new BmobQuery<>();
//        queryCity.addWhereEqualTo("cityName",cityName);
//        queryCity.findObjects(context, new FindListener<City>() {
//            @Override
//            public void onSuccess(List<City> list) {
//                if(list!=null&list.size()>0){
//                    City city = list.get(0);
//                    SubCategory subCategory = new SubCategory();
//                    subCategory.setObjectId(subCategoryId);
//                    queryFoodsByCityAndSubcategory(context,city,subCategory,listener);
//
//                } else {
//                    Toast.makeText(context,"查询出错，请稍后重试！",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                Log.i("TAG:FoodManager",i+":"+s);
//            }
//        });
//    }
//
//    private static void queryFoodsByCityAndSubcategory(Context context, City city, SubCategory subCategory, FindListener<Food> listener) {
//        BmobQuery<Food> queryFood = new BmobQuery<>();
//        queryFood.addWhereEqualTo("city",city);
//        queryFood.addWhereEqualTo("subCategory",subCategory);
//        queryFood.include("shop");
//        queryFood.order("-likerNum");
//        queryFood.findObjects(context,listener);
    }

    public static void getFoodById(Context context, String foodId, FindListener<Food> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Food> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", foodId);
        query.include("author,shop.author");
        query.findObjects(context, listener);
    }

    public static void getFoodsByShop(Context context, Shop shop, FindListener<Food> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<Food> query = new BmobQuery<>();
        query.addWhereEqualTo("shop", shop);
        query.include("author,shop.author");
        query.findObjects(context, listener);
    }

    public static void getFoodsByCityAndPage(Context context, CityNameBean cityNameBean, String street, int pageno, FindListener<Food> listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }
        City city = new City();
        city.setObjectId(cityNameBean.getCityId());

        BmobQuery<Food> query = new BmobQuery<>();

        if (!"全部".equals(street)) {
            BmobQuery<Shop> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("street", street);
            query.addWhereMatchesQuery("shop", "Shop", innerQuery);
        }

        query.addWhereEqualTo("city", city);
        query.include("author,shop.author");
        query.order("-createdAt");
        query.setLimit(15);
        query.setSkip(pageno * 15);
        query.findObjects(context, listener);
    }


}
