package com.qianxia.sijia.fragment;


import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.listener.OnFragmentSwitchListener;
import com.qianxia.sijia.ui.MainActivity;
import com.qianxia.sijia.ui.PostFoodActivity;
import com.qianxia.sijia.ui.RegistActivity;
import com.qianxia.sijia.util.EditInputUtil;
import com.qianxia.sijia.util.MD5;
import com.qianxia.sijia.util.NetUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

    @Bind(R.id.et_login_username)
    EditText etUserName;
    @Bind(R.id.et_login_password)
    EditText etPassword;

    private OnFragmentSwitchListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnFragmentSwitchListener) context;

    }

    @Override
    protected View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    protected void init() {

    }

    @Override
    protected void LazyLoad() {
        toolbar.setTitle("思佳");
        toolbar.setSubtitle("登录");
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

    @OnClick(R.id.btn_login_login)
    public void login(View view) {
        if (EditInputUtil.isEmpty(etPassword, etUserName)) {
            toast("请输入用户名和密码！");
            return;
        }
        if (!NetUtil.isNetworkAvailable(mContext)) {
            toast("网络不给力，请稍候再试");
            return;
        }
        String username = etUserName.getText().toString();
        final String password = MD5.toHexString(etPassword.getText().toString().getBytes());
        BmobChatUser user = new BmobChatUser();
        user.setUsername(username);
        user.setPassword(password);
        bmobUserManager.login(user, new SaveListener() {
            @Override
            public void onSuccess() {
                baseActivity.upCurrentUserLocation(new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        if (mListener != null) {
                            mListener.fragmentSwitch(Constant.EnumFragment.FRAGMENT_MY, new CountFragment());
                            mListener.refreshHeardView();
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toastAndaLog("登录失败，请稍候再试", i, s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                if (i == 101) {
                    toast("用户名或者密码错误，请重新输入");
                } else {
                    toastAndaLog("网络繁忙，请稍后重试", i, s);
                }
            }
        });
    }

    @OnClick(R.id.btn_login_regist)
    public void startRegist(View view) {
        jumpTo(RegistActivity.class, true);
    }
}
