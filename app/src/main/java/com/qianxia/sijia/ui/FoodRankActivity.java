package com.qianxia.sijia.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.FoodRankListAdapter;
import com.qianxia.sijia.adapter.FoodRankPagerAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.manager.FoodManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import cn.bmob.v3.listener.FindListener;

public class FoodRankActivity extends BaseActivity {
    @Bind(R.id.rg_foodrank_category)
    RadioGroup radioGroup;
    @Bind(R.id.viewpager_foodrank)
    ViewPager viewPager;
    @Bind(R.id.scrollview_foodrank_category)
    HorizontalScrollView scrollView;

    private int subCategoryPosition;
    private ArrayList<SubCategoryTable> list;
    private FoodRankPagerAdapter pagerAdapter;
    private int screenHalf;
    private Map<String, AdapterHoder> mapAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_food_rank);
    }

    @Override
    protected void init() {
        subCategoryPosition = getIntent().getIntExtra("position", -10);
        list = getIntent().getParcelableArrayListExtra("list");

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        screenHalf = outMetrics.widthPixels / 2;

        initToolbar();
        initRadioGroup();
        initViewPager();
    }

    private void initToolbar() {
        toolbar.setTitle("佳食排行");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
            }
        });
    }

    private void setRadioGroupListener() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rbChecked = (RadioButton) group.findViewById(checkedId);
                int scrollX = scrollView.getScrollX();
                int left = rbChecked.getLeft();
                int width = rbChecked.getWidth() / 2;
                int leftScreen = left - scrollX;
                scrollView.smoothScrollBy(leftScreen - screenHalf + width, 0);
                viewPager.setCurrentItem(group.indexOfChild(rbChecked), true);
            }
        });
    }

    private void initViewPager() {
        ArrayList<View> views = new ArrayList<>();
        mapAdapter = new HashMap<String, AdapterHoder>();
        for (SubCategoryTable subCategoryTable : list) {
            AdapterHoder adapterHoder = new AdapterHoder();
            View view = getLayoutInflater().inflate(R.layout.inflate_foodrank_viewpager, viewPager, false);
            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_foodrank_viewpager);
//            final FrameLayout emptyView = (FrameLayout) view.findViewById(R.id.fl_foodrank_lisetview_empty);
            final ProgressBar progressbar = (ProgressBar) view.findViewById(R.id.progressbar_foodrank_listview_empty);
            final TextView tvEmpty = (TextView) view.findViewById(R.id.tv_foodrank_listview_empty);

            final ArrayList<Food> foods = new ArrayList<>();
            final FoodRankListAdapter adapter = new FoodRankListAdapter(this, foods);
            LinearLayoutManager layoutManager = new LinearLayoutManager(FoodRankActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new FoodRankListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(FoodRankActivity.this, FoodDetailActivity.class);
                    Food food = foods.get(position);
                    intent.putExtra("food", food);
                    jumpTo(intent, false);
                }
            });
            String subCategoryId = subCategoryTable.getId();
            adapterHoder.subCategoryId = subCategoryId;
            adapterHoder.adapter = adapter;
            adapterHoder.progressBar = progressbar;
            adapterHoder.tvEmpty = tvEmpty;
            mapAdapter.put(subCategoryId, adapterHoder);
            views.add(view);
        }
        pagerAdapter = new FoodRankPagerAdapter(this, views);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(radioGroup.getChildAt(position).getId());
                subCategoryPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void refreshFoodData(final ProgressBar progressbar, final TextView tvEmpty, final FoodRankListAdapter adapter, String subCategoryId) {
        FoodManager.getFoodsByCityAndSubcategory(this, SijiaApplication.selectedCity, subCategoryId, new FindListener<Food>() {
            @Override
            public void onSuccess(List list) {
                if (list != null && list.size() > 0) {
                    adapter.addAll(list, true);
                    tvEmpty.setVisibility(View.INVISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                progressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(int i, String s) {
                toast("网络繁忙稍后重试");
                Log.i("TAG:initViewPager", i + ":" + s);
            }
        });
    }

    private void initRadioGroup() {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        for (SubCategoryTable subCategoryTable : list) {
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(layoutParams);
            rb.setPadding(16, 0, 16, 0);
            rb.setButtonDrawable(android.support.v7.appcompat.R.drawable.abc_list_selector_background_transition_holo_light);
//            XmlResourceParser xrp = getResources().getXml(R.color.selector_radiobtn_foodrank);
//            ColorStateList colorStateList = new ColorStateList();
            ColorStateList csl = getResources().getColorStateList(R.color.selector_radiobtn_foodrank);
            if (csl != null) {
                rb.setTextColor(csl);
            }

            rb.setText(subCategoryTable.getName());
//            rb.setTextColor(getResources().getColor(R.color.selector_radiobtn_foodrank));
            radioGroup.addView(rb);
        }
        setRadioGroupListener();
//        radioGroup.check(radioGroup.getChildAt(0).getId());

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        for (SubCategoryTable subCategoryTable : list) {
            AdapterHoder aHolder = mapAdapter.get(subCategoryTable.getId());
            refreshFoodData(aHolder.progressBar, aHolder.tvEmpty, aHolder.adapter, aHolder.subCategoryId);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(subCategoryPosition);
            }
        }, 5);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_toolbar_search) {
            jumpTo(SearchActivity.class, false);
        }

        return super.onOptionsItemSelected(item);
    }

    class AdapterHoder {
        String subCategoryId;
        TextView tvEmpty;
        FoodRankListAdapter adapter;
        ProgressBar progressBar;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
