package com.qianxia.sijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.manager.PopupWindowManager;

/**
 * Created by Administrator on 2016/11/24.
 */
public class SijiaSpinner extends LinearLayout implements View.OnClickListener {
    private TextView textView;
    private ImageView imageView;
    private SijiaSpinnerAdapter adapter;

    public SijiaSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = inflate(context, R.layout.inflate_sijiaspinner, this);
        addView(view);
        initView(view);
    }

    private void initView(View view) {
        textView = (TextView) view.findViewById(R.id.tv_sijiaspinner);
        imageView = (ImageView) view.findViewById(R.id.iv_sijiaspinner);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        View view = inflate(getContext(), R.layout.item_listview_popupwindow_city, this);
        PopupWindow popWindow = new PopupWindow(view, v.getWidth(), LayoutParams.MATCH_PARENT, true);
        popWindow.showAsDropDown(this);
    }


    private class SijiaSpinnerAdapter {
    }
}
