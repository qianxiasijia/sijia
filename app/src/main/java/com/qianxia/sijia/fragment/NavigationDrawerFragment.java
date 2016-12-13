package com.qianxia.sijia.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qianxia.sijia.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends BaseFragment {


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the header_photos_fooddetail_refreshlsv for this fragment
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void LazyLoad() {

    }


}
