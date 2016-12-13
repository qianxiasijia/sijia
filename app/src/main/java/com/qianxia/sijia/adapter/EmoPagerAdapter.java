package com.qianxia.sijia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */
public class EmoPagerAdapter extends SijiaBasePagerAdapter<View> {
    public EmoPagerAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mDatas.get(position);
        container.addView(view);
        return view;
    }
}
