package com.qianxia.sijia.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.CitySearchDialogFragment;

import butterknife.ButterKnife;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by tarena on 2016/10/18.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BmobUserManager bmobUserManager;
    protected BmobChatManager bmobChatManager;

    protected BmobDB bmobDB;
    public Toolbar toolbar;

    protected Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        bmobUserManager = BmobUserManager.getInstance(this);
        bmobChatManager = BmobChatManager.getInstance(this);
        bmobDB = BmobDB.create(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        SijiaApplication.activities.add(this);
        initialLayout();
        init();
    }

    protected void init() {

    }

    private void initialLayout() {
        String clazzName = getClass().getSimpleName();
        String name = "activity_" + clazzName.substring(0, clazzName.lastIndexOf("Activity")).toLowerCase();
        int resId = getResources().getIdentifier(name, "layout", getPackageName());
        if (resId != 0) {
            setContentView(resId);
        } else {
            setMyContentView();
        }
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.layout_toolbar_activity);
    }

//    /**
//     * 设置标题栏
//     *
//     * @param logo app图标id 值为0，则不设置
//     * @param icon 导航图标id 值为0，则不设置
//     * @param title 标题内容
//     * @param subTitle 副标题内容
//     * @param listener 菜单项监听
//     */
//    @Deprecated
//    public void initToolbar(int logo, int icon, String title, String subTitle, Toolbar.OnMenuItemClickListener listener) {
//        if(logo!=0){
//            toolbar.setLogo(logo);
//        }
//        if(title!=null){
//            toolbar.setTitle(title);
//        }
//        if(subTitle!=null){
//            toolbar.setSubtitle(subTitle);
//        }
////        setSupportActionBar(toolbar);
//        if(icon!=0){
//            toolbar.setNavigationIcon(icon);
//        }
//        if(listener!=null){
//            toolbar.setOnMenuItemClickListener(listener);
//        }
//    }

    protected abstract void setMyContentView();

    public void log(String log) {
        if (Constant.DEBUG) {
            Log.i("SIJIADEBUG", "从" + this + "打印出来的调试日记：" + log);
        }
    }

    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            toast.setText(text);
            toast.show();
        }
    }

    public void log(int code, String log) {
        log("错误信息：" + code + ":" + log);
    }

    public void toastAndLog(String text, String log) {
        toast(text);
        log(log);
    }

    public void toastAndLog(String text, int code, String log) {
        toast(text);
        log(code, log);
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
        if (isFinish) {
            finish();
        }
    }

    public void jumpTo(Intent intent, boolean isFinish) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
        if (isFinish) {
            finish();
        }
    }

    public void upCurrentUserLocation(UpdateListener listener) {
        SijiaUser user = bmobUserManager.getCurrentUser(SijiaUser.class);
        SijiaUser newUser = new SijiaUser();
        newUser.setLocation(SijiaApplication.lastPosition);
        newUser.setCity(SijiaApplication.localCity);
        if (user != null) {
//            log("upCurrentUserLocation:"+String.valueOf(user.isAllowFinded()));
            newUser.setAllowFinded(user.isAllowFinded());
            newUser.setGender(user.isGender());
            newUser.update(this, user.getObjectId(), listener);
        }
    }


    private void showCitySelectDialogFragment() {
        FragmentManager manager = getSupportFragmentManager();
        CitySearchDialogFragment dialogFragment = (CitySearchDialogFragment) manager.findFragmentByTag("citySearchDialog");
        if (dialogFragment == null) {
            dialogFragment = CitySearchDialogFragment.getInstance(this);
            dialogFragment.show(manager, "citySearchDialog");
        } else {
            FragmentTransaction ft = manager.beginTransaction();
            ft.show(dialogFragment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.REQUESTCODE_DATABASE) {
            boolean isPermissived = true;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    isPermissived = false;
                    toast("需要获取授权建立本地数据库");
                    break;
                }
            }
            if (isPermissived) {
                showCitySelectDialogFragment();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BaseActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

}
