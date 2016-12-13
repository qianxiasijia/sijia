package com.qianxia.sijia.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/8.
 */
public class SijiaImagesAtuoTurnView extends FrameLayout {

    private final LruCache<String, Bitmap> memoryCache;
    private ViewPager viewPager;
    private LinearLayout indicatorsContainer;
    private PagerAdapter adapter;

    private ArrayList<String> resUrls;
    private boolean flag = true;
    private Handler handler;
    private DBUtil dbUtil;

    public SijiaImagesAtuoTurnView(Context context, ArrayList<String> imgUrls) {
        super(context);
        dbUtil = new DBUtil(context);
        handler = new Handler(Looper.getMainLooper());
        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        if (imgUrls != null && imgUrls.size() > 0) {
            resUrls = new ArrayList<>(imgUrls);
            resUrls.add(0, imgUrls.get(imgUrls.size() - 1));
            resUrls.add(imgUrls.get(0));
        }
        View view = LayoutInflater.from(context).inflate(R.layout.header_photos_fooddetail_refreshlsv, this, false);
        addView(view);
        initView(view);

        start();
    }

    /**
     * 启动轮播
     */
    private void start() {
        flag = true;
//        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    int pos = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(pos + 1);
                    postDelayed(this, 3000);
                }
            }
        }, 2000);
    }

    private void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_fooddetail_header1);
        indicatorsContainer = (LinearLayout) view.findViewById(R.id.ll_indicator_fooddetail_header1);
        adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return resUrls.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView iv = new ImageView(getContext());
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //TODO 添加缓存
                SiJiaImageLoader.loadImage(resUrls.get(position), iv, dbUtil, memoryCache);

                container.addView(iv);
                return iv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((ImageView) object);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    viewPager.setCurrentItem(adapter.getCount() - 2);
                    setIndicator(indicatorsContainer.getChildCount() - 1);
                } else if (position == adapter.getCount() - 1) {
                    viewPager.setCurrentItem(1);
                    setIndicator(0);
                } else {
                    setIndicator(position - 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // viewpager进行事件传播时，如果有OnTouchListener的监听时，会优先调用OnTouchListener的onTouch方法
        // 然后根据onTouch方法的返回值决定是否将该事件传播给onTouchEvent方法，返回值为true时传播，返回值为false时不传播
        viewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    stop();
                }
                if (action == MotionEvent.ACTION_UP) {
                    start();
                }
                return false;
            }
        });


        //添加指示器
        for (int i = 0; i < adapter.getCount() - 2; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setImageResource(R.drawable.banner_dot_selected);

            //位iv添加外边距
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
            params.setMargins(margin, margin, margin, margin);
            iv.setLayoutParams(params);

            indicatorsContainer.addView(iv);
        }

        setIndicator(0);

    }

    /**
     * 停止轮播
     */
    private void stop() {
        flag = false;
        handler.removeCallbacksAndMessages(null);

    }


    //设定第pos个指示器显示橘红色的圆点图片
    private void setIndicator(int pos) {
        for (int i = 0; i < indicatorsContainer.getChildCount(); i++) {
            ImageView iv = (ImageView) indicatorsContainer.getChildAt(i);
            if (pos == i) {
                iv.setImageResource(R.drawable.banner_dot_pressed);
            } else {
                iv.setImageResource(R.drawable.banner_dot_selected);
            }
        }

    }
}
