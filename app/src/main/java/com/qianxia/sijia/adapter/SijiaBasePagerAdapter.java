package com.qianxia.sijia.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tarena on 2016/9/8.
 */
public abstract class SijiaBasePagerAdapter<T> extends PagerAdapter {
    protected LayoutInflater mInflater;
    protected List<T> mDatas = new ArrayList<>();
    protected Context mContext;

    public SijiaBasePagerAdapter(Context context, List<T> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = list;
    }

    public void addAll(List<T> list, boolean flag) {
        if (flag) {
            mDatas.clear();
        }
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public abstract Object instantiateItem(ViewGroup container, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
