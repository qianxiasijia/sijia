package com.qianxia.sijia.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qianxia.sijia.R;
import com.qianxia.sijia.ui.BaseActivity;

import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.db.BmobDB;

/**
 * Created by tarena on 2016/9/8.
 */
public abstract class BaseFragment extends Fragment {

    protected Context mContext;
    protected BmobUserManager bmobUserManager;
    protected BmobChatManager bmobCharManager;
    protected BmobDB bmobDB;
    protected BaseActivity baseActivity;
    protected boolean isVisible;
    protected boolean isPrepared;
    protected Toolbar toolbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        baseActivity = (BaseActivity) mContext;
        toolbar = baseActivity.toolbar;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bmobUserManager = BmobUserManager.getInstance(mContext);
        bmobCharManager = BmobChatManager.getInstance(mContext);
        bmobDB = BmobDB.create(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        String clazzName = mContext.getClass().getSimpleName();
        if (clazzName.contains("Fragment")) {
            String name = "fragment_" + clazzName.substring(0, clazzName.indexOf("Fragment"));
            int resId = getResources().getIdentifier(name, "layout", mContext.getPackageName());
            if (resId != 0) {
                view = inflater.inflate(resId, container, false);
            } else {
                view = onCreateMyView(inflater, container, savedInstanceState);
            }
        } else {
            view = onCreateMyView(inflater, container, savedInstanceState);
        }
        ButterKnife.bind(this, view);
        init();
        isPrepared = true;
        if (isVisible) {
            LazyLoad();
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onInvisible() {
    }

    ;

    protected void onVisible() {
        if (isPrepared) {
            LazyLoad();
        }
    }

    protected abstract void LazyLoad();

    protected abstract View onCreateMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void init();

    public void log(String log) {
        baseActivity.log(log);
    }

    public void log(int code, String log) {
        baseActivity.log(code, log);
    }

    public void toast(String text) {
        baseActivity.toast(text);
    }

    public void toastAndLog(String text, String log) {
        baseActivity.toastAndLog(text, log);
    }

    public void toastAndaLog(String text, int code, String log) {
        baseActivity.toastAndLog(text, code, log);
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        Intent intent = new Intent(mContext, clazz);
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);


        if (isFinish) {
            getActivity().finish();
        }
    }

    public void jumpTo(Intent intent, boolean isFinish) {
        startActivity(intent);
        baseActivity.overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

        if (isFinish) {
            getActivity().finish();
        }
    }

}
