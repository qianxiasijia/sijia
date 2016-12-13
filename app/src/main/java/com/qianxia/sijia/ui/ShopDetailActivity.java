package com.qianxia.sijia.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.EmoGridViewAdapter;
import com.qianxia.sijia.adapter.EmoPagerAdapter;
import com.qianxia.sijia.adapter.ShopCommentAdapter;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.Like;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopComment;
import com.qianxia.sijia.entry.ShopReply;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.DeleteDialogFrament;
import com.qianxia.sijia.listener.OnDeleteCommentListener;
import com.qianxia.sijia.listener.OnDialogClickListener;
import com.qianxia.sijia.listener.OnReplayListener;
import com.qianxia.sijia.listener.OnShopCommentsLoadedListener;
import com.qianxia.sijia.manager.CommentManager;
import com.qianxia.sijia.manager.FoodManager;
import com.qianxia.sijia.util.EmoUtil;
import com.qianxia.sijia.util.NetUtil;
import com.qianxia.sijia.view.SijiaImagesAtuoTurnView;
import com.qianxia.sijia.view.SijiaTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShopDetailActivity extends BaseActivity implements OnReplayListener<ShopComment> {
    @Bind(R.id.refreshlsv_fooddetail)
    PullToRefreshListView refreshListView;
    @Bind(R.id.ll_fooddetail_emocontainer)
    LinearLayout emoContainer;
    @Bind(R.id.ll_fooddetail_sendreplay)
    LinearLayout sendLayout;
    @Bind(R.id.et_fooddetail_sendreplay)
    EditText etReplayContent;


    private ListView listView;
    private ShopCommentAdapter adapter;
    private Shop shop;
    private ArrayList<ShopComment> shopComments;
    private LinearLayout emoLayout;
    private ViewPager viewPager;

    private ShopComment mComment;
    private String replayTitle;

    private SijiaUser currentUser;
    private boolean hasZan;
    private Like mLike;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_shop_detail);
    }

    @Override
    protected void init() {
        currentUser = bmobUserManager.getCurrentUser(SijiaUser.class);
        shop = (Shop) getIntent().getSerializableExtra("shop");
        initToolbar();
        initHeaderView();
        initFootView();
        initEmoLayout();

    }

    private void initFootView() {
        View view = getLayoutInflater().inflate(R.layout.inflate_refreshlistview_footview, listView, false);
        listView.addFooterView(view);
    }

    private void initToolbar() {
        toolbar.setTitle("佳店详情");
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

    private void initEmoLayout() {
        emoLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_layout_emolayout_postcomment, emoContainer, false);
        viewPager = (ViewPager) emoLayout.findViewById(R.id.viewpager_postcomment_emolayout_emo);
        List<View> views = new ArrayList<>();
        int pageno = EmoUtil.emos.size() % 21 == 0 ? EmoUtil.emos.size() / 21 : EmoUtil.emos.size() / 21 + 1;
        for (int i = 0; i < pageno; i++) {
            View view = getLayoutInflater().inflate(R.layout.item_viewpager_postcomment_emogridview, emoLayout, false);
            GridView gridView = (GridView) view.findViewById(R.id.gv_postcomment_emo_gridview);
            int end = Math.min((i + 1) * 21, EmoUtil.emos.size());
            List<String> emos = EmoUtil.emos.subList(i * 21, end);
            EmoGridViewAdapter adapter = new EmoGridViewAdapter(this, emos);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    etReplayContent.append(EmoUtil.getSpannableString((String) parent.getAdapter().getItem(position)));
                }
            });
            views.add(gridView);
        }
        EmoPagerAdapter emoPagerAdapter = new EmoPagerAdapter(this, views);
        viewPager.setAdapter(emoPagerAdapter);


    }


    private void initHeaderView() {
        List<BmobFile> pics = shop.getPics();
        ArrayList<String> imgUrls = new ArrayList<>();
        if (pics == null) {
            imgUrls = null;
        } else {
            for (BmobFile bmobFile : pics) {
                String imgUrl = bmobFile.getUrl();
                imgUrls.add(imgUrl);
            }
        }
        listView = refreshListView.getRefreshableView();
        listView.setSelector(new BitmapDrawable());

        if (imgUrls == null) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.food_sample);
            listView.addHeaderView(iv);
        } else {
            SijiaImagesAtuoTurnView headerPhotosView = new SijiaImagesAtuoTurnView(this, imgUrls);
            listView.addHeaderView(headerPhotosView);
        }
        View headerContentView = getLayoutInflater().inflate(R.layout.header_content_shopdetail_refresh, listView, false);
        initHeaderContentView(headerContentView);
        View headerCommentView = getLayoutInflater().inflate(R.layout.header_shopdetail_refresh_header4, listView, false);
        initHeaderCommentView(headerCommentView);
        View headerFoodsView = getLayoutInflater().inflate(R.layout.header_shopdetail_refresh_header3, listView, false);
        initHeaderFoodsView(headerFoodsView);
        listView.addHeaderView(headerContentView);
        listView.addHeaderView(headerFoodsView);
        listView.addHeaderView(headerCommentView);
        shopComments = new ArrayList<>();
        adapter = new ShopCommentAdapter(this, shopComments, this);
        adapter.setDeleteCommentListener(new OnDeleteCommentListener() {
            @Override
            public void onDeleteComment(final int position) {
                DeleteDialogFrament dialog = new DeleteDialogFrament();
                dialog.setOnDialogClickListener(new OnDialogClickListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        ShopComment comment = adapter.getItem(position);
                        CommentManager.deleteShopComment(ShopDetailActivity.this, comment, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                refresh();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toastAndLog("网络繁忙，请稍后重试", i, s);
                            }
                        });

                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {

                    }
                });
                dialog.show(getSupportFragmentManager(), "deleteDialog");
            }
        });
        listView.setAdapter(adapter);

        setRefreshListener();

    }

    private void initHeaderFoodsView(final View headerFoodsView) {
        final LinearLayout foodsContainer = (LinearLayout) headerFoodsView.findViewById(R.id.ll_shopdetail_foodcontainer);
        FoodManager.getFoodsByShop(this, shop, new FindListener<Food>() {
            @Override
            public void onSuccess(List<Food> list) {
                if (list != null && list.size() > 0) {
                    for (final Food food : list) {
                        View foodView = getLayoutInflater().inflate(R.layout.inflate_shopdetail_fooditem, foodsContainer, false);
                        TextView tvFoodName = (TextView) foodView.findViewById(R.id.tv_foodname_shopdetail_header3);
                        TextView tvPrice = (TextView) foodView.findViewById(R.id.tv_price_shopdetail_header3);
                        SijiaTextView tvCommentNum = (SijiaTextView) foodView.findViewById(R.id.tv_commentnum_shopdetail_header3);
                        SijiaTextView tvLiketNum = (SijiaTextView) foodView.findViewById(R.id.tv_likenum_shopdetail_header3);
                        tvFoodName.setText(food.getName());
                        float price = food.getPrice() == null ? 0.0f : food.getPrice();
                        tvPrice.setText("参考价格：￥" + price);
                        int likeNum = 0;
                        if (food.getLikerNum() != null) {
                            likeNum = food.getLikerNum();
                        }
                        tvLiketNum.setText(String.valueOf(likeNum));
                        int commentNum = 0;
                        if (food.getCommentNum() != null) {
                            commentNum = food.getCommentNum();
                        }
                        tvCommentNum.setText(String.valueOf(commentNum));
                        foodView.setBackgroundResource(R.drawable.input_bg);
                        foodView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ShopDetailActivity.this, FoodDetailActivity.class);
                                intent.putExtra("from", "shop");
                                intent.putExtra("food", food);
                                jumpTo(intent, true);
                            }
                        });
                        foodsContainer.addView(foodView);
                    }

                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initHeaderCommentView(View headerCommentView) {

        tvCommentNum = (SijiaTextView) headerCommentView.findViewById(R.id.tv_commentnum_shopdetail_header2);
        tvLikerNum = (SijiaTextView) headerCommentView.findViewById(R.id.tv_likenum_shopdetail_header2);
//        Button btnWriteComment = (Button) headerCommentView.findViewById(R.id.btn_writecomment_shopdetail_header2);

//        btnWriteComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bmobUserManager.getCurrentUser()==null){
//                    toast("需要先登录哟^o^");
//                    return;
//                }
//                Intent intent =  new Intent(ShopDetailActivity.this,PostCommentActivity.class);
//                intent.putExtra("from","shop");
//                intent.putExtra("shop",shop);
//                jumpTo(intent,false);
//            }
//        });
        tvLikerNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                log("tvLikerNum.onClick");
                if (currentUser == null) {
                    toast("登录后点赞吧");
                    return;
                }
//                    log("hasZan:"+String.valueOf(hasZan));
                if (hasZan) {
                    mLike.delete(ShopDetailActivity.this, new DeleteListener() {
                        @Override
                        public void onSuccess() {
//                            log("deletelike");
                            int likerNum = shop.getLikerNum();
                            likerNum -= 1;
                            shop.setLikerNum(likerNum);
                            final int finalLikerNum = likerNum;
                            shop.update(ShopDetailActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    tvLikerNum.setText(String.valueOf(finalLikerNum));
                                    setTvlikeDrawable(false);
                                    hasZan = false;
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                } else {
                    final Like like = new Like();
                    like.setShopId(shop.getObjectId());
                    like.setLikerId(currentUser.getObjectId());
                    like.save(ShopDetailActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            BmobQuery<Like> query = new BmobQuery<>();
                            query.addWhereEqualTo("shopId", shop.getObjectId());
                            query.addWhereEqualTo("likerId", currentUser.getObjectId());
                            query.findObjects(ShopDetailActivity.this, new FindListener<Like>() {
                                @Override
                                public void onSuccess(List<Like> list) {
                                    if (list != null && list.size() > 0) {
                                        mLike = list.get(0);
                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                            int likerNum = shop.getLikerNum();
                            likerNum += 1;
                            shop.setLikerNum(likerNum);
                            final int finalLikerNum = likerNum;
                            shop.update(ShopDetailActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    tvLikerNum.setText(String.valueOf(finalLikerNum));
                                    setTvlikeDrawable(true);
                                    hasZan = true;
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }

            }
        });
    }

    private void setRefreshListener() {
        refreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout refreshLables = refreshListView.getLoadingLayoutProxy(true, false);
        refreshLables.setPullLabel("重新刷新...");
        refreshLables.setRefreshingLabel("正在刷新...");
        refreshLables.setReleaseLabel("松开刷新...");
        ILoadingLayout loadinglables = refreshListView.getLoadingLayoutProxy(false, true);
        loadinglables.setPullLabel("加载更多...");
        loadinglables.setReleaseLabel("松开加载...");
        loadinglables.setRefreshingLabel("正在加载");
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                log("onPullDownToRefresh.refresh");
                page = 0;
                CommentManager.getShopCommentsByPage(ShopDetailActivity.this, shop, 0, new OnShopCommentsLoadedListener() {
                    @Override
                    public void onShopCommentsLoaded(List<ShopComment> shopComments) {
                        adapter.addAll(shopComments, true);
                        refreshListView.onRefreshComplete();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
                page += 1;
                CommentManager.getShopCommentsByPage(ShopDetailActivity.this, shop, page, new OnShopCommentsLoadedListener() {
                    @Override
                    public void onShopCommentsLoaded(List<ShopComment> shopComments) {
                        adapter.addAll(shopComments, false);
                        refreshView.onRefreshComplete();
                    }
                });
            }
        });
    }

    SijiaTextView tvCommentNum;
    SijiaTextView tvLikerNum;

    private void initHeaderContentView(View headerContentView) {
        TextView tvShopName = (TextView) headerContentView.findViewById(R.id.tv_shopname_shopdetail_header2);
        TextView tvAddress = (TextView) headerContentView.findViewById(R.id.tv_address_shopdetail_header2);
        TextView tvOpenTime = (TextView) headerContentView.findViewById(R.id.tv_opentime_shopdetail_header2);
        TextView tvDescription = (TextView) headerContentView.findViewById(R.id.tv_description_shopdetail_header2);
        TextView tvTitle = (TextView) headerContentView.findViewById(R.id.tv_anthor_shopdetail_header2);
        TextView tvTelephone = (TextView) headerContentView.findViewById(R.id.tv_telephone_shopdetail_header2);

        String telephone = shop.getTelephone();
        if (telephone == null) {

            tvTelephone.setText("联系电话：(暂无)");
        } else {
            tvTelephone.setText("联系电话：" + telephone);
        }

        SijiaUser user = shop.getAuthor();

        String title = "";
        if (user != null) {
            log("user:" + user.toString());
            title = "该佳店信息由 " + user.getUsername() + " 提供";
        } else {
            title = "该佳店信息由 " + "Test" + " 提供";
        }
        SpannableString ss = new SpannableString(title);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), 6, title.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(ss);

        tvShopName.setText(shop.getName());
        tvAddress.setText("地址：" + shop.getAddress());
        String openTime = shop.getOpenTime();
        if (openTime == null) {
            openTime = "";
        }
        tvOpenTime.setText("营业时间：" + openTime);
        tvDescription.setText(shop.getDescription());
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopDetailActivity.this, MapActivity.class);
                intent.putExtra("from", "shop");
                intent.putExtra("shop", shop);
                jumpTo(intent, false);
            }
        });
    }


    private void setCommentAndLikerNum() {
        int commentNum = 0;
        if (shop.getCommentNum() != null) {
            commentNum = shop.getCommentNum();
        }
        tvCommentNum.setText(String.valueOf(commentNum));
        int likerNum = 0;
        if (shop.getLikerNum() != null) {
            likerNum = shop.getLikerNum();
        }
        tvLikerNum.setText(String.valueOf(String.valueOf(likerNum)));
        if (currentUser == null) {
            setTvlikeDrawable(false);
        } else {
            final Like like = new Like();
            like.setShopId(shop.getObjectId());
            like.setLikerId(currentUser.getObjectId());
//            log(like.toString());
            BmobQuery<Like> query = new BmobQuery<>();
            query.addWhereEqualTo("likerId", like.getLikerId());
            query.addWhereEqualTo("shopId", like.getShopId());
            query.findObjects(this, new FindListener<Like>() {
                @Override
                public void onSuccess(List<Like> list) {
                    if (list != null && list.size() > 0) {
                        mLike = list.get(0);
                        hasZan = true;
                        setTvlikeDrawable(true);
                    } else {
                        setTvlikeDrawable(false);
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    private void setTvlikeDrawable(boolean isZan) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());
        Drawable drawable;
        if (isZan) {
            drawable = getResources().getDrawable(R.drawable.ic_like_select);
        } else {
            drawable = getResources().getDrawable(R.drawable.ic_like);
        }
        drawable.setBounds(0, 0, size, size);
        tvLikerNum.setCompoundDrawables(drawable, null, null, null);
    }

    @OnClick(R.id.iv_fooddetail_emos)
    public void showEmos(View view) {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etReplayContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (emoContainer.getChildCount() == 0) {
            emoContainer.addView(emoLayout);
        } else {
            emoContainer.removeAllViews();
        }
    }

    @OnClick(R.id.btn_fooddetail_sendreplay)
    public void sendReplay(View view) {
        String content = etReplayContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            toast("要说点什么呢？");
            return;
        }
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状态不良，请稍后重试");
            return;
        }
        ShopReply replay = new ShopReply();
        replay.setTime(System.currentTimeMillis());
        SijiaUser user = bmobUserManager.getCurrentUser(SijiaUser.class);
        replay.setAuthor(user);
        replay.setContent(content);
        replay.setShopComment(mComment);
        replay.setTitle(replayTitle);
        replay.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("评论发布成功");
                etReplayContent.setText("");
                sendLayout.setVisibility(View.INVISIBLE);
                refresh();
            }

            @Override
            public void onFailure(int i, String s) {
                toastAndLog("保存失败，请稍后再试", i, s);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();

    }


    private void refresh() {
        BmobQuery<Shop> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", shop.getObjectId());
        query.findObjects(this, new FindListener<Shop>() {
            @Override
            public void onSuccess(List<Shop> list) {
                if (list != null && list.size() > 0) {
                    shop = list.get(0);
                    setCommentAndLikerNum();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        CommentManager.getShopCommentsByPage(this, shop, 0, new OnShopCommentsLoadedListener() {
            @Override
            public void onShopCommentsLoaded(List<ShopComment> shopComments) {
                adapter.addAll(shopComments, true);
//                refreshListView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onReplay(ShopComment comment, String title) {
        sendLayout.setVisibility(View.VISIBLE);
        mComment = comment;
        replayTitle = title;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] location = new int[2];
            sendLayout.getLocationInWindow(location);
            int top = location[1];
            if (ev.getY() < top) {
                if (sendLayout.getVisibility() == View.VISIBLE) {
                    sendLayout.setVisibility(View.INVISIBLE);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (emoContainer.getChildCount() > 0) {
                emoContainer.removeAllViews();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_toolbar_backhome) {
            finishAffinity();
            jumpTo(MainActivity.class, false);
            return true;
        }
        if (currentUser == null) {
            toast("需要先登录哟^o^");
            return true;
        }
        switch (id) {
            case R.id.menu_toolbar_comment:
                jumpToWrite();
                break;
            case R.id.menu_toolbar_collect:
                collect();
                break;
            case R.id.menu_toolbar_share:
                share();
                break;
            case R.id.menu_toolbar_search:
                jumpTo(SearchActivity.class, false);
        }
        return true;
    }

    private void share() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();

// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("来自思佳的分享");
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("https://www.baidu.com");
// text是分享文本，所有平台都需要这个字段

        oks.setText(shop.getName() + "\n" + shop.getDescription());
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(shop.getPics().get(0).getUrl());
        oks.setTitleUrl(shop.getPics().get(0).getUrl());
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://www.baidu.com");
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("希望对大家有用~");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("https://www.baidu.com");

// 启动分享GUI
        oks.show(this);


    }

    private void collect() {
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状况不佳，再试一次吧");
            return;
        }
        Collect collect = new Collect();
        collect.setContent(shop.getDescription());
        collect.setShopId(shop.getObjectId());
        collect.setImgUrl(shop.getPics().get(0).getUrl());
        collect.setTime(System.currentTimeMillis());
        collect.setName(shop.getName());
        collect.setUserId(currentUser.getObjectId());
        collect.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                toast("收藏成功");
            }

            @Override
            public void onFailure(int i, String s) {
                toastAndLog("网络繁忙，稍后再试一下吧", i, s);
            }
        });
    }

    private void jumpToWrite() {
        Intent intent = new Intent(ShopDetailActivity.this, PostCommentActivity.class);
        intent.putExtra("from", "shop");
        intent.putExtra("shop", shop);
        jumpTo(intent, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
