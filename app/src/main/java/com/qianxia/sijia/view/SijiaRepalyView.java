package com.qianxia.sijia.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.FoodReply;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopReply;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.EmoUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.util.TimeUtil;

/**
 * Created by Administrator on 2016/11/12.
 */
public class SijiaRepalyView extends LinearLayout {

    private final LruCache<String, Bitmap> memoryCache;
    private CircleImageView ivAvatar;
    private TextView tvContent;
    private TextView tvTitle;
    private TextView tvTime;
    private DBUtil dbUtil;
    private ShopReply shopReply;
    private FoodReply foodReply;

    public SijiaRepalyView(Context context, Object replay) {
        super(context);
        dbUtil = new DBUtil(context);

        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        View view = LayoutInflater.from(context).inflate(R.layout.inflate_view_replay, this, false);
        init(view);
        addView(view);
        if (replay instanceof ShopReply) {
            shopReply = (ShopReply) replay;
            setContent(shopReply);
        } else if (replay instanceof FoodReply) {
            foodReply = (FoodReply) replay;
            setContent(foodReply);
        }
    }

    private void init(View view) {
        ivAvatar = (CircleImageView) view.findViewById(R.id.iv_replay_avatar);
        tvTime = (TextView) view.findViewById(R.id.tv_replay_time);
        tvTitle = (TextView) view.findViewById(R.id.tv_replay_title);
        tvContent = (TextView) view.findViewById(R.id.tv_replay_content);
    }

    private void setContent(ShopReply replay) {
        tvTime.setText(TimeUtil.getTime(replay.getTime()));
        String title = replay.getTitle();
        int start = 0;
        if (title.contains("&")) {
            start = title.split("&")[0].length();
            title = title.replace("&", "回复");
        }
        SpannableString ss = new SpannableString(title);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (title.contains("回复")) {
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), start, start + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvTitle.setText(ss);
        tvContent.setText(EmoUtil.getSpannableString(replay.getContent()));
        String imgUrl = replay.getAuthor().getAvatar();
        if (TextUtils.isEmpty(imgUrl)) {
            ivAvatar.setImageResource(R.drawable.icon_my_photo);
        } else {
            if (dbUtil.isBitmapExist(imgUrl)) {
                dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                    @Override
                    public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                        ivAvatar.setImageBitmap(bitmap);
                    }
                });
            } else {
                ImageLoader.getInstance().displayImage(imgUrl, ivAvatar, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        dbUtil.saveBitmap(s, bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }
    }

    private void setContent(FoodReply replay) {
        tvTime.setText(TimeUtil.getTime(replay.getTime()));
        String title = replay.getTitle();
        int start = 0;
        if (title.contains("&")) {
            start = title.split("&")[0].length();
            title = title.replace("&", "回复");
        }
        SpannableString ss = new SpannableString(title);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.accent)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (title.contains("回复")) {
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary)), start, start + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvTitle.setText(ss);
        tvContent.setText(EmoUtil.getSpannableString(replay.getContent()));
        String imgUrl = replay.getAuthor().getAvatar();
        if (TextUtils.isEmpty(imgUrl)) {
            ivAvatar.setImageResource(R.drawable.icon_my_photo);
        } else {
            SiJiaImageLoader.loadImage(imgUrl, ivAvatar, dbUtil, memoryCache);
        }
    }


}
