package com.qianxia.sijia.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.qianxia.sijia.R;
import com.qianxia.sijia.ui.MainActivity;
import com.qianxia.sijia.ui.MapActivity;
import com.qianxia.sijia.ui.PostFoodActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends BaseFragment {

    private MainActivity activity;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void LazyLoad() {
        toolbar.setTitle("思佳");
        toolbar.setSubtitle("附近寻佳");
        TextView btnPost = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        btnPost.setText("我来推荐");
        Toolbar.LayoutParams params = (Toolbar.LayoutParams) btnPost.getLayoutParams();
        Animation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1500);
        animation.setFillAfter(true);
//        btnPost.startAnimation(animation);
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
        btnPost.invalidate();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bmobUserManager.getCurrentUser() == null) {
                    toast("需要先登录才能推荐哦");
                    return;
                }
                jumpTo(PostFoodActivity.class, false);
            }
        });

//        button.invalidate();
    }

    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    protected void init() {

    }

    @OnClick(R.id.ll_nearby_friend)
    public void findFriend(View view) {
        if (bmobUserManager.getCurrentUser() == null) {
            toast("请先登录");
            return;
        }
        Intent intent = new Intent(mContext, MapActivity.class);
        intent.putExtra("from", "findfriend");
        jumpTo(intent, false);
    }

    @OnClick(R.id.ll_nearby_place)
    public void findPlace(View view) {
        Intent intent = new Intent(mContext, MapActivity.class);
        intent.putExtra("from", "findplace");
        jumpTo(intent, false);
    }

    @OnClick(R.id.ll_nearby_other)
    public void findOther(View view) {
        Intent intent = new Intent(mContext, MapActivity.class);
        intent.putExtra("from", "findother");
        jumpTo(intent, false);
    }


}
