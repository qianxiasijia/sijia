package com.qianxia.sijia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/11/25.
 */
public class PhotoViewPagerAdapter extends SijiaBasePagerAdapter<View> {
    public PhotoViewPagerAdapter(Context context, List<View> list) {
        super(context, list);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mDatas.get(position);
        container.addView(view);
        return view;
    }
}
