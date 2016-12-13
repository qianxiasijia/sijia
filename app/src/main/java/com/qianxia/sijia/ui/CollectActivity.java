package com.qianxia.sijia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.CollectAdapter;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.fragment.DeleteDialogFrament;
import com.qianxia.sijia.listener.OnDialogClickListener;
import com.qianxia.sijia.manager.FoodManager;
import com.qianxia.sijia.manager.ShopManager;
import com.qianxia.sijia.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class CollectActivity extends BaseActivity {
    @Bind(R.id.recyclerview_collect)
    RecyclerView recyclerView;
    @Bind(R.id.swiperefreshlayout_collect)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tv_collect_empty)
    TextView tvEmpty;

    private List<Collect> collects;
    private CollectAdapter adapter;
    private boolean isLoading;
    private int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_collect);
    }

    @Override
    protected void init() {
        initToolbar();
        initRecycleView();
    }

    private void initRecycleView() {


        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        collects = new ArrayList<>();
        adapter = new CollectAdapter(this, collects);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListner(new CollectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Collect collect = collects.get(position);
                if (collect.getFoodId() != null) {
                    jumpToFoodDetail(collect.getFoodId());
                } else if (collect.getShopId() != null) {
                    jumpToShopDetail(collect.getShopId());
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                DeleteDialogFrament dialog = new DeleteDialogFrament();
                dialog.setOnDialogClickListener(new OnDialogClickListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        Collect collect = collects.get(position);
                        collect.delete(CollectActivity.this, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                collects.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position - 1, collects.size() - 1);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toastAndLog("网络繁忙，请稍后再试", i, s);
                            }
                        });

                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.show(getSupportFragmentManager(), "deleteCollection");
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    return;
                }
                log("onScrollStateChanged");
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition < (pageNo + 1) * 10 - 1) {
//                    toast("没有更多了");
                    return;
                }
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        collects.add(null);
                        adapter.notifyItemInserted(collects.size() - 1);
                        pageNo += 1;
                        queryDataByPage(pageNo, new FindListener<Collect>() {
                            @Override
                            public void onSuccess(List<Collect> list) {
                                if (list != null && list.size() > 0) {
                                    collects.remove(collects.size() - 1);
                                    adapter.addAll(list, false);
                                    isLoading = false;
                                }
                            }

                            @Override
                            public void onError(int i, String s) {
                                toastAndLog("网络繁忙，请稍后再试", i, s);
                                isLoading = false;
                            }
                        });

                    }
                }

            }
        });
    }

    private void jumpToShopDetail(String shopId) {
        ShopManager.getShopById(this, shopId, new FindListener<Shop>() {
            @Override
            public void onSuccess(List<Shop> list) {
                if (list != null && list.size() > 0) {
                    Shop shop = list.get(0);
                    Intent intent = new Intent(CollectActivity.this, ShopDetailActivity.class);
                    intent.putExtra("shop", shop);
                    jumpTo(intent, false);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void jumpToFoodDetail(String foodId) {
        FoodManager.getFoodById(this, foodId, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
                    Food food = list.get(0);
                    Intent intent = new Intent(CollectActivity.this, FoodDetailActivity.class);
                    intent.putExtra("food", food);
                    jumpTo(intent, false);
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
            }
        });
    }

    private void refresh() {
        pageNo = 0;
        queryDataByPage(pageNo, new FindListener<Collect>() {
            @Override
            public void onSuccess(List<Collect> list) {
                if (list != null && list.size() > 0) {
                    adapter.addAll(list, true);
                    tvEmpty.setVisibility(View.GONE);

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

    private void queryDataByPage(int num, FindListener<Collect> listener) {
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状况不良，请稍后重试");
            return;
        }

        BmobQuery<Collect> query = new BmobQuery<>();
        query.addWhereEqualTo("userId", bmobUserManager.getCurrentUserObjectId());
        query.setSkip(num * 10);
        query.setLimit(10);
        query.order("-createdAt");
        query.findObjects(this, listener);
    }

    private void initToolbar() {
        toolbar.setTitle("我的收藏");
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
