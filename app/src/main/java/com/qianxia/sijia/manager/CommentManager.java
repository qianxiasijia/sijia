package com.qianxia.sijia.manager;

import android.content.Context;
import android.widget.Toast;

import com.qianxia.sijia.R;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.FoodComment;
import com.qianxia.sijia.entry.FoodReply;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopComment;
import com.qianxia.sijia.entry.ShopReply;
import com.qianxia.sijia.listener.OnFoodCommentsLoadedListener;
import com.qianxia.sijia.listener.OnShopCommentsLoadedListener;
import com.qianxia.sijia.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by tarena on 2016/9/14.
 */
public class CommentManager {

    public static void getFoodCommentsByPage(final Context context, Food food, final int page, final OnFoodCommentsLoadedListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<FoodComment> query = new BmobQuery<>();
        query.addWhereEqualTo("food", food);
        query.setLimit(10);
        query.setSkip(10 * page);
        query.include("author");
        query.order("-createdAt");
        query.findObjects(context, new FindListener<FoodComment>() {
            @Override
            public void onSuccess(List<FoodComment> list) {
                if (list != null) {
                    if (page == 0 && list.size() == 0) {
                        FoodComment comment = new FoodComment();
                        comment.setTime(-10);
                        list.add(comment);
                    }
                    listener.onFoodCommentsLoaded(list);
                } else {
                    Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteFoodComment(final Context context, final FoodComment comment, final UpdateListener listetner) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        final BmobQuery<FoodComment> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", comment.getObjectId());
        query.include("food");
        query.findObjects(context, new FindListener<FoodComment>() {
            @Override
            public void onSuccess(List<FoodComment> list) {
                if (list != null && list.size() > 0) {
                    final FoodComment commentWithFood = list.get(0);
                    commentWithFood.delete(context, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            deleteFoodReplay(commentWithFood, context);
                            Food food = commentWithFood.getFood();
                            int commentNum = food.getCommentNum();
                            food.setCommentNum(commentNum - 1);
                            food.update(context, listetner);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private static void deleteFoodReplay(FoodComment comment, final Context context) {
        BmobQuery<FoodReply> bmobQuery = new BmobQuery<FoodReply>();
        bmobQuery.addWhereEqualTo("foodComment", comment);
        bmobQuery.findObjects(context, new FindListener<FoodReply>() {
            @Override
            public void onSuccess(List<FoodReply> list) {
                if (list != null && list.size() > 0) {
                    for (FoodReply reply : list) {
                        reply.delete(context, new DeleteListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    public static void getShopCommentsByPage(final Context context, Shop shop, final int page, final OnShopCommentsLoadedListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobQuery<ShopComment> query = new BmobQuery<>();
        query.addWhereEqualTo("shop", shop);
        query.setLimit(10);
        query.setSkip(10 * page);
        query.include("author");
        query.order("-createdAt");
        query.findObjects(context, new FindListener<ShopComment>() {
            @Override
            public void onSuccess(List<ShopComment> list) {
                if (list != null) {
                    if (page == 0 && list.size() == 0) {
                        ShopComment comment = new ShopComment();
                        comment.setTime(-10);
                        list.add(comment);
                    }
                    listener.onShopCommentsLoaded(list);
                } else {
                    Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteShopComment(final Context context, final ShopComment comment, final UpdateListener listener) {
        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络状况不良，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        final BmobQuery<ShopComment> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", comment.getObjectId());
        query.include("shop");
        query.findObjects(context, new FindListener<ShopComment>() {
            @Override
            public void onSuccess(List<ShopComment> list) {
                if (list != null && list.size() > 0) {
                    final ShopComment commentWithShop = list.get(0);
                    commentWithShop.delete(context, new DeleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                            deleteShopReplay(commentWithShop, context);
                            Shop shop = commentWithShop.getShop();
                            int commentNum = shop.getCommentNum();
                            shop.setCommentNum(commentNum - 1);
                            shop.update(context, listener);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(context, "网络繁忙，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private static void deleteShopReplay(ShopComment comment, final Context context) {
        BmobQuery<ShopReply> bmobQuery = new BmobQuery<ShopReply>();
        bmobQuery.addWhereEqualTo("shopComment", comment);
        bmobQuery.findObjects(context, new FindListener<ShopReply>() {
            @Override
            public void onSuccess(List<ShopReply> list) {
                if (list != null & list.size() > 0) {
                    for (ShopReply reply : list) {
                        reply.delete(context, new DeleteListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

}
