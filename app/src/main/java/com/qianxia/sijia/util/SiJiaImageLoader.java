package com.qianxia.sijia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;

/**
 * Created by Administrator on 2016/11/2.
 */
public class SiJiaImageLoader {


    public static void loadImage(final String imgUrl, final ImageView imageView, final DBUtil dbUtil, final LruCache<String, Bitmap> memoryCache) {
        if (imgUrl.equals(imageView.getTag())) {
            return;
        }
//        imageView.setImageBitmap(null);
        Bitmap bitmap = memoryCache.get(imgUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

//        if (dbUtil.isBitmapExist(imgUrl)) {
//            dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap) {
//                    if (imgUrl.equals(imageView.getTag())) {
//                        imageView.setImageBitmap(bitmap);
//                        memoryCache.put(imgUrl, bitmap);
//                    }
//                }
//            });
//            return;
//        } else {

        ImageLoader.getInstance().displayImage(imgUrl, new ImageViewAware(imageView), SijiaApplication.commOptionsCache, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (s.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                    memoryCache.put(s, bitmap);
//                        dbUtil.saveBitmap(s, bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        imageView.setTag(imgUrl);
    }
//    }
}
