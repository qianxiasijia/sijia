package com.qianxia.sijia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.MainCategoryAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnMainCategoriesLoadedListener;
import com.qianxia.sijia.manager.CategoryManager;
import com.qianxia.sijia.manager.PopupWindowManager;
import com.qianxia.sijia.ui.CitySearchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/10/23.
 */
public class FoodFragment extends BaseFragment {

    @Bind(R.id.listview_food_category)
    ListView listView;
    ArrayList<MainCategoryTable> mainCategoryTables;
    MainCategoryAdapter adapter;
    private TextView btnCitySelect;

    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

    @Override
    protected void init() {
//        log("foodfragment"+String.valueOf(bmobUserManager.getCurrentUser(SijiaUser.class).isAllowFinded()));
        mainCategoryTables = new ArrayList<>();
        adapter = new MainCategoryAdapter(mContext, mainCategoryTables);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainCategoryTable mainCategoryTable = (MainCategoryTable) parent.getAdapter().getItem(position);
                PopupWindowManager.showSubcategoryPopupWindow(mContext, listView, mainCategoryTable.getId());
            }
        });
    }

    @Override
    protected void LazyLoad() {
        initToolbar();
        refresh();
    }

    private void initToolbar() {
        toolbar.setTitle("思佳");
        toolbar.setSubtitle("分类排行");
        btnCitySelect = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        btnCitySelect.setText(SijiaApplication.selectedCity.getCityName());
        btnCitySelect.setVisibility(View.VISIBLE);
        Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        btnCitySelect.startAnimation(animation);
        Toolbar.LayoutParams params = (Toolbar.LayoutParams) btnCitySelect.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
        btnCitySelect.invalidate();
        btnCitySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CitySearchActivity.class);
                startActivityForResult(intent, Constant.REQUESTCODE_CITYSEARCH);
                baseActivity.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
            }
        });

    }

    private void refresh() {
        CategoryManager.getMainCategories(mContext, new OnMainCategoriesLoadedListener() {
            @Override
            public void onMainCategoriesLoaded(List<MainCategoryTable> mainCategoryTables) {
                adapter.addAll(mainCategoryTables, true);
//                log("mainCategoryTables"+mainCategoryTables.toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        btnCitySelect.setText(SijiaApplication.selectedCity.getCityName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//                    log("foodFreagment+resultCode:"+resultCode);
        if (resultCode == Constant.RESULT_CITYSELECTED) {
            String cityStr = data.getStringExtra("city");
            btnCitySelect.setText(cityStr);
        }


    }
}
