package com.qianxia.sijia.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.Collect;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.ui.CollectActivity;
import com.qianxia.sijia.ui.PostFoodActivity;
import com.qianxia.sijia.ui.PostListActivity;
import com.qianxia.sijia.ui.SettingActivity;
import com.qianxia.sijia.ui.UserInfoActivity;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.view.CircleImageView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class CountFragment extends BaseFragment {

    @Bind(R.id.include_user_info)
    RelativeLayout layoutHeader;

    private TextView tvUserName, tvNickName;
    private CircleImageView ivAvatar;

    private SijiaUser currentUser;
    private DBUtil dbUtil;

    public CountFragment() {
        // Required empty public constructor
    }

    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_count, container, false);
    }

    @Override
    protected void init() {

        dbUtil = new DBUtil(mContext);
        tvUserName = (TextView) layoutHeader.findViewById(R.id.tv_count_username);
        tvNickName = (TextView) layoutHeader.findViewById(R.id.tv_count_nickname);
        ivAvatar = (CircleImageView) layoutHeader.findViewById(R.id.iv_count_atavar);
//        BmobChatUser user = bmobUserManager.getCurrentUser();

    }


    @Override
    protected void LazyLoad() {
        initToolbar();
        refresh();
    }

    private void initToolbar() {

        toolbar.setTitle("思佳");
        toolbar.setSubtitle("我的思佳");
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


    }

    private void refresh() {
        currentUser = bmobUserManager.getCurrentUser(SijiaUser.class);
        tvUserName.setText(currentUser.getUsername());
        tvNickName.setText(currentUser.getNick());
        String imgUrl = currentUser.getAvatar();
        if (imgUrl != null) {
            if (dbUtil.isBitmapExist(imgUrl)) {
                dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                    @Override
                    public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                        ivAvatar.setImageBitmap(bitmap);
                    }
                });
            } else {
                ImageLoader.getInstance().displayImage(imgUrl, ivAvatar, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        dbUtil.saveBitmap(s, bitmap);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
        }
    }

    @OnClick(R.id.ll_logout_count)
    public void logout(View view) {
        SijiaApplication.logout();
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume");
        refresh();
    }

    @OnClick(R.id.include_user_info)
    public void editUserInfo(View view) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("from", "CountFragment");
        intent.putExtra("user", currentUser);
        jumpTo(intent, false);
    }

    @OnClick(R.id.ll_count_mysetting)
    public void editSetting(View view) {
        jumpTo(SettingActivity.class, false);
    }

    @OnClick(R.id.ll_count_mycollection)
    public void showCollection() {
        jumpTo(CollectActivity.class, false);
    }

    @OnClick(R.id.ll_count_mypost)
    public void showMyPost() {
        jumpTo(PostListActivity.class, false);
    }
}
