package com.qianxia.sijia.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import java.util.List;

/**
 * Created by tarena on 2016/9/9.
 */
public class SijiaFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments;

    public SijiaFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public void replaceFragment(int index, Fragment Fragment) {
        if (mFragments.size() > 0 && index >= 0 && index < mFragments.size()) {
            mFragments.remove(index);
            mFragments.add(index, Fragment);
            notifyDataSetChanged();
        }
    }

    public List<Fragment> getFragments() {
        return mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
