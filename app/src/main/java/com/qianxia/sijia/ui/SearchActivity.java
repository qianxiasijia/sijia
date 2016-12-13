package com.qianxia.sijia.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.ResultAdapter;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.SearchResult;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.listener.OnFindResultListener;
import com.qianxia.sijia.manager.FoodManager;
import com.qianxia.sijia.manager.SearchManager;
import com.qianxia.sijia.manager.ShopManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends BaseActivity {

    @Bind(R.id.et_search_search)
    EditText etSearch;
    @Bind(R.id.tv_search_cancel)
    TextView tvCancel;
    @Bind(R.id.recyclerview_search)
    RecyclerView recyclerView;
    @Bind(R.id.tv_search_info)
    TextView tvInfo;

    private List<SearchResult> results;
    private ResultAdapter adapter;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void init() {
        from = getIntent().getStringExtra("from");
        initRecyclerView();
        if ("postfood".equals(from)) {
            tvInfo.setVisibility(View.VISIBLE);
            setAdapterListener();
            setEditTextListener();
        } else {
            setSearchEditTextListener();
            setSearchAdapterListener();
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
    }

    private void setSearchAdapterListener() {
        adapter.setOnItemSelectListener(new ResultAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(View view, int position) {
                SearchResult result = adapter.getItem(position);
                String foodId = result.getFoodId();
                String shopId = result.getShopId();
                if (!TextUtils.isEmpty(foodId)) {
                    jumpToFoodDetail(foodId);
                } else {
                    jumpToShopDetail(shopId);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                return;
            }
        });
    }

    private void jumpToShopDetail(String shopId) {
        ShopManager.getShopById(this, shopId, new FindListener<Shop>() {
            @Override
            public void onSuccess(List<Shop> list) {
                if (list != null && list.size() > 0) {
                    Shop shop = list.get(0);
                    Intent intent = new Intent(SearchActivity.this, ShopDetailActivity.class);
                    intent.putExtra("shop", shop);
                    jumpTo(intent, false);
                } else {
                    toast("网络繁忙，请稍后重试");
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
            }
        });
    }

    private void jumpToFoodDetail(String foodId) {
        FoodManager.getFoodById(this, foodId, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
                    Food food = list.get(0);
                    Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
                    intent.putExtra("food", food);
                    jumpTo(intent, false);
                } else {
                    toast("网络繁忙，请稍后重试");
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
            }
        });
    }

    private void setSearchEditTextListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {

                if (TextUtils.isEmpty(s)) {
                    adapter.addAll(results, true);
                } else {
                    SearchManager.getShopResult(SearchActivity.this, s.toString(), new OnFindResultListener() {
                        @Override
                        public void findResult(final List<SearchResult> listShop) {
                            SearchManager.getFoodResult(SearchActivity.this, s.toString(), new OnFindResultListener() {
                                @Override
                                public void findResult(List<SearchResult> list) {
                                    listShop.addAll(list);
                                    adapter.addAll(listShop, true);
                                }
                            });
                        }
                    });

                }

            }
        });
    }

    private void setEditTextListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    adapter.addAll(results, true);
                } else {
                    SearchManager.getShopResult(SearchActivity.this, s.toString(), new OnFindResultListener() {
                        @Override
                        public void findResult(List<SearchResult> list) {
                            adapter.addAll(list, true);
                        }
                    });
                }
            }
        });
    }

    private void setAdapterListener() {
        adapter.setOnItemSelectListener(new ResultAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(View view, int position) {
                SearchResult result = adapter.getItem(position);
                String shopId = result.getShopId();
                jumpToShopDetail(shopId);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                SearchResult result = adapter.getItem(position);
                ShopManager.getShopById(SearchActivity.this, result.getShopId(), new FindListener<Shop>() {
                    @Override
                    public void onSuccess(List<Shop> list) {
                        if (list != null && list.size() > 0) {
                            Intent data = new Intent();
                            data.putExtra("shop", list.get(0));
                            log("setResult" + list.get(0).getName());
                            setResult(Constant.REQUESTCODE_SEARCHRESULTT, data);
                            finish();
                            overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
                        } else {
                            toast("网络繁忙，请稍后重试");
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        toastAndLog("网络繁忙，请稍后重试", i, s);
                    }
                });
            }
        });
    }

    private void initRecyclerView() {
        results = new ArrayList<>();
        adapter = new ResultAdapter(this, results);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
