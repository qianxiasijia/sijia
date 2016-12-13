package com.qianxia.sijia.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/8/28.
 */
public class SijiListView extends ListView {
    public SijiListView(Context context) {
        super(context);
    }

    public SijiListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SijiListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
