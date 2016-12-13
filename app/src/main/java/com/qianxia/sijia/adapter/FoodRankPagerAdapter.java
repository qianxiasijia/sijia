package com.qianxia.sijia.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/17.
 */
public class FoodRankPagerAdapter extends SijiaBasePagerAdapter<View> {


    public FoodRankPagerAdapter(Context context, List<View> list) {
        super(context, list);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mDatas.get(position);
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        container.addView(view);
        return view;
    }
}
