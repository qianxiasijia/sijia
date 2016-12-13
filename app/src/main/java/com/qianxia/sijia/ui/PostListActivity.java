package com.qianxia.sijia.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.PostListAdapter;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class PostListActivity extends BaseActivity {

    @Bind(R.id.swiperefreshlayout_mypost)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_mypost)
    RecyclerView recyclerView;
    @Bind(R.id.tv_mypost_empty)
    TextView tvEmpty;

    private List<Food> foods;
    private PostListAdapter adapter;
    private boolean isLoading;
    private int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_post_list);
    }

    @Override
    protected void init() {
        initToolbar();
        initRecyclerView();
    }

    private void initRecyclerView() {
        swipeRefreshLayout.setColorSchemeColors(R.color.primary, R.color.primary_dark, R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));


        foods = new ArrayList<>();
        adapter = new PostListAdapter(foods, this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListner(new PostListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food food = foods.get(position);
                Intent intent = new Intent(PostListActivity.this, FoodDetailActivity.class);
                intent.putExtra("food", food);
                jumpTo(intent, false);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    return;
                }
//                log("onScrollStateChanged");
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition < (pageNo + 1) * 10 - 1) {
//                    toast("没有更多了");
                    return;
                }
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        foods.add(null);
                        adapter.notifyItemInserted(foods.size() - 1);
                        pageNo += 1;
                        queryDataByPage(pageNo, new FindListener<Food>() {
                            @Override
                            public void onSuccess(List<Food> list) {
                                if (list != null && list.size() > 0) {
                                    foods.remove(foods.size() - 1);
                                    adapter.addAll(list, false);
                                    isLoading = false;
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                toastAndLog("网络繁忙，请稍后再试", i, s);
                            }
                        });

                    }
                }
            }
        });
    }

    private void queryDataByPage(int num, FindListener<Food> listener) {
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状况不良，请稍后重试");
            return;
        }

        BmobQuery<Food> query = new BmobQuery<>();
        query.addWhereEqualTo("author", bmobUserManager.getCurrentUser(SijiaUser.class));
        query.setSkip(num * 10);
        query.setLimit(10);
        query.include("author,shop.author");
        query.order("-createdAt");
        query.findObjects(this, listener);
    }

    private void refresh() {
        pageNo = 0;
        queryDataByPage(pageNo, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
                    adapter.addAll(list, true);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后再试", i, s);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initToolbar() {
        toolbar.setTitle("我的发表");
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
