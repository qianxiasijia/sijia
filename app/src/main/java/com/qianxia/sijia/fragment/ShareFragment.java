package com.qianxia.sijia.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.ShareFoodsAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.Region;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnRegionLoadedListener;
import com.qianxia.sijia.manager.FoodManager;
import com.qianxia.sijia.manager.RegionManager;
import com.qianxia.sijia.ui.FoodDetailActivity;
import com.qianxia.sijia.ui.PostFoodActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends BaseFragment {

    @Bind(R.id.recyclerview_sharefragment_food)
    RecyclerView recyclerView;
    @Bind(R.id.swiperefreshlayout_sharefragment)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tv_sharefragment_empty)
    TextView tvEmpty;


    private ShareFoodsAdapter adapter;
    private List<Food> foodDatas;
    private List<Food> foods;
    private List<String> regionNames;
    private List<String> streets;
    private List<Region> regions;
    private ArrayAdapter<String> disAdapter;
    private ArrayAdapter<String> strAdapter;
    private boolean isAllowOnResumeRefresh;
    private boolean isLoading;
    private int pageno;
    private String street = "全部";
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Spinner disSpinner, strSpinner;

    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    protected void init() {
//        log("foodfragment"+String.valueOf(bmobUserManager.getCurrentUser(SijiaUser.class).isAllowFinded()));

        regions = new ArrayList<>();
        foodDatas = new ArrayList<>();
        initSwipeRefreshLayout();
        initRecycleView();
        initSpinner();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    private void initSpinner() {
        regionNames = new ArrayList<>();
        streets = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_sharefragment_headerview, recyclerView, false);
        disSpinner = (Spinner) view.findViewById(R.id.spinner_sharefragment_district);
        strSpinner = (Spinner) view.findViewById(R.id.spinner_sharefragment_street);
        disAdapter = new ArrayAdapter<String>(mContext, R.layout.tv_for_spinner, regionNames);
        disAdapter.setDropDownViewResource(R.layout.item_sharefragment_spinner_tv);
        disSpinner.setAdapter(disAdapter);
        disSpinner.setDropDownVerticalOffset(30);
        strAdapter = new ArrayAdapter<String>(mContext, R.layout.tv_for_spinner, streets);
        strAdapter.setDropDownViewResource(R.layout.item_sharefragment_spinner_tv);
        strSpinner.setAdapter(strAdapter);
        strSpinner.setDropDownVerticalOffset(30);
        adapter.setHeaderView(view);
        disSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshStrAdapter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        strSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                street = streets.get(position);
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void refreshStrAdapter(int position) {
        Region region = regions.get(position);
        List<String> neighborhoodNames = region.getNeighborhoods();
        if (neighborhoodNames != null) {
            streets.clear();
            streets.addAll(neighborhoodNames);
            strAdapter.notifyDataSetChanged();
            if (streets.size() > 0) {
                street = streets.get(0);
                refresh();
            }
        }
    }

    private void initRecycleView() {
        foods = new ArrayList<>();
        adapter = new ShareFoodsAdapter(mContext, foods);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new ShareFoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mContext, FoodDetailActivity.class);
                intent.putExtra("food", adapter.getItem(position));
                log("sharefragment:" + adapter.getItem(position).getShop().toString());
                jumpTo(intent, false);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    imageLoader.resume();
                    log("imageLoader.resume");
                } else {
                    imageLoader.pause();
                    log("imageLoader.pause");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    return;
                }
                int lastVisibilePosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibilePosition < (pageno + 1) * 15 - 2) {
                    return;
                }
                if (lastVisibilePosition + 1 == adapter.getItemCount()) {
                    if (!isLoading) {
                        foods.add(null);
                        adapter.notifyItemInserted(foods.size() - 1);
                        pageno += 1;
                        FoodManager.getFoodsByCityAndPage(mContext, SijiaApplication.selectedCity, street, pageno, new FindListener<Food>() {
                            @Override
                            public void onSuccess(List<Food> list) {
                                if (list != null) {
                                    foodDatas.addAll(list);
                                    foods.remove(foods.size() - 1);
                                    adapter.addAll(list, false);
                                    isLoading = false;
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                toastAndaLog("网络繁忙，请稍后再试", i, s);
                                isLoading = false;
                            }
                        });

                    }
                }
            }
        });
    }

    @Override
    protected void LazyLoad() {
        log("LazyLoad" + ":selectedCity:" + SijiaApplication.selectedCity.getCityName());
        initToolbar();
        initSpinnerData();
        refresh();
        isAllowOnResumeRefresh = true;

    }

    private void initSpinnerData() {
        RegionManager.getDistrictsByCity(mContext, SijiaApplication.selectedCity, new OnRegionLoadedListener() {
            @Override
            public void onRegionLoaded(List<Region> list) {
                Region temp = new Region();
                temp.setName("全部");
                temp.setNeighborhoods(Arrays.asList(new String[]{"全部"}));
                list.add(0, temp);
                regions = list;
                regionNames.clear();
                for (Region region : list) {
                    regionNames.add(region.getName());
                }
                disAdapter.notifyDataSetChanged();
                disSpinner.setSelection(0);
                refreshStrAdapter(0);
            }
        });
    }


    private void initToolbar() {
        toolbar.setTitle("思佳");
        toolbar.setSubtitle("佳食分享");

        TextView btnPost = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        btnPost.setText("我来推荐");
        Toolbar.LayoutParams params = (Toolbar.LayoutParams) btnPost.getLayoutParams();
        Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1500);
        animation.setFillAfter(true);
        btnPost.startAnimation(animation);
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
        btnPost.invalidate();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bmobUserManager.getCurrentUser() == null) {
                    toast("需要先登录才能推荐哦");
                    return;
                }
                jumpTo(PostFoodActivity.class, false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        log("shareFragment:onResume");
        if (isAllowOnResumeRefresh) {
            refresh();
        }
    }

    private void refresh() {
        pageno = 0;
        FoodManager.getFoodsByCityAndPage(mContext, SijiaApplication.selectedCity, street, pageno, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
//                    foodDatas.addAll(list);
                    adapter.addAll(list, true);
                    swipeRefreshLayout.setRefreshing(false);
                    tvEmpty.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndaLog("网络繁忙，请稍后再试", i, s);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
