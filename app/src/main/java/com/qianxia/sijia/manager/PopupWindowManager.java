package com.qianxia.sijia.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.SijiaSubCategoryAdapter;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.listener.OnSubCategoriesLoadedListener;
import com.qianxia.sijia.ui.BaseActivity;
import com.qianxia.sijia.ui.FoodRankActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarena on 2016/9/13.
 */
public class PopupWindowManager {


    public static void showSubcategoryPopupWindow(final Context context, final View parent, String mainCategoryId) {
        final View contentView = View.inflate(context, R.layout.inflate_foodcategory_popupwindow, null);

        final ListView popListView = (ListView) contentView.findViewById(R.id.listview_popupwindow_subcategory);
        List<SubCategoryTable> subCategoryTableList = new ArrayList<>();
        final SijiaSubCategoryAdapter adapter = new SijiaSubCategoryAdapter(context, subCategoryTableList);
        popListView.setAdapter(adapter);

        CategoryManager.getSubCategories(context, mainCategoryId, new OnSubCategoriesLoadedListener() {
            @Override
            public void onSubCategoriesLoaded(final List<SubCategoryTable> list) {
                if (list != null) {
//                    Log.i("TAG:list.toString",list.toString());
                    adapter.addAll(list, true);

                    //        parent.getLocationOnScreen(parentLoc);
//                    final int parentHeight = parent.getHeight();
//                    contentView.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.UNSPECIFIED);
//                    // 计算contentView的高宽
//                    int windowHeight = contentView.getMeasuredHeight();
//                    int windowWidth = contentView.getMeasuredWidth();
////        final int windowWidth = contentView.getMeasuredWidth();
//                    int offHeight = parentHeight/2-windowHeight/2;
//                    // 判断需要向上弹出还是向下弹出显示
//                    final boolean isNeedScroll = (parentHeight<windowHeight);
//                    if (isNeedScroll) {
//                        windowHeight = parentHeight;
//                        contentView.measure(windowWidth, View.MeasureSpec.EXACTLY+windowHeight);
//                        contentView.header_photos_fooddetail_refreshlsv(0,0,contentView.getMeasuredWidth(),contentView.getMeasuredHeight());
//                    }
                    contentView.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.UNSPECIFIED);
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, context.getResources().getDisplayMetrics());
                    int h = contentView.getMeasuredHeight() * list.size();
                    int height = h > size ? size : h;
                    final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, height, true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setAnimationStyle(R.style.popinanimation);


                    popupWindow.showAtLocation(parent, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0);
//                    Log.i("TAG:PopupWindow","showSubcategoryPopupWindow.show");
                    popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SubCategoryTable subCategoryTable = (SubCategoryTable) parent.getAdapter().getItem(position);
                            Intent intent = new Intent(context, FoodRankActivity.class);
                            intent.putExtra("position", position);
                            intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) list);
                            context.startActivity(intent);
                            ((BaseActivity) context).overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
                            popupWindow.dismiss();
                        }
                    });


                }
            }
        });

    }
}


//    public static void showSubCategoryPopupWindow(final Context context, View view, final int mainCategoryPosition,final OnFragmentSwitchListener mFragmentSwitchListener){
//        View contentview = View.inflate(context,R.header_photos_fooddetail_refreshlsv.inflate_foodcategory_popupwindow,null);
//        final PopupWindow popupWindow = new PopupWindow(contentview,ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT,true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        ListView popListView = (ListView) contentview.findViewById(R.id.childFoodCategoryListViewId);
//        final SijiaSubCategoryAdapter adapter = new SijiaSubCategoryAdapter(context);
//        popListView.setAdapter(adapter);
//        final List<SubCategory> subCategories = CategroyManager.getSubCategroys(mainCategoryPosition);
//        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            //单击副分类跳转到排行列表
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                popupWindow.dismiss();
//                String cityName = CityManager.getCities().get(0).getCityName();
//                FoodRankFragment foodRankFragment = FoodRankFragment.getInstance(cityName,mainCategoryPosition, subCategories.get(position).getCategoryName());
//                mFragmentSwitchListener.fragmentSwitch(0,foodRankFragment);
//                Log.i("TAG","fragmentSwitch");
//
//            }
//        });
//        adapter.setDatas(subCategories);
//        int[] locations = new int[2];
//        view.getLocationOnScreen(locations);
//        popupWindow.showAtLocation(view,Gravity.NO_GRAVITY,locations[0]+popupWindow.getWidth(),locations[1]);
//    }

//    public static void showCityChangePopupWindow(Context context,View view, final OnCityChangedListener listener){
//        View contentView = View.inflate(context,R.header_photos_fooddetail_refreshlsv.inflate_foodcategory_popupwindow,null);
//        PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        ListView popListView = (ListView) contentView.findViewById(R.id.childFoodCategoryListViewId);
//        CityChangeAdapter adapter = new CityChangeAdapter(context);
//        adapter.setDatas(CityManager.getCities());
//        popListView.setAdapter(adapter);
//        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                City city = (City) parent.getAdapter().getItem(position);
//                listener.updateFoodRankAdapter(city.getCityName());
//            }
//        });
//        popupWindow.showAsDropDown(view);
//    }
//
//    public interface OnCityChangedListener{
//      void updateFoodRankAdapter(String cityName);
//    }

//}
