package com.qianxia.sijia.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.SijiaDatePickerFragment;
import com.qianxia.sijia.util.EditInputUtil;
import com.qianxia.sijia.util.MD5;
import com.qianxia.sijia.util.NetUtil;
import com.qianxia.sijia.util.PinYinUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    @Bind(R.id.tv_regist_date)
    EditText tvBirthday;
    @Bind(R.id.et_regist_username)
    EditText etUserName;
    @Bind(R.id.et_regist_password)
    EditText etPassword;
    @Bind(R.id.et_regist_passwordcheck)
    EditText etPasswordCheck;
    @Bind(R.id.rg_regist_gender)
    RadioGroup rgGender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_regist);
    }

    @Override
    protected void init() {
        toolbar.setTitle("欢迎注册");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
    }

    @OnClick(R.id.tv_regist_date)
    public void pickDate(View view) {
//        SijiaDatePickerFragment.getInstance(new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                tvBirthday.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
//            }
//        }).show(getSupportFragmentManager(),"datePicker");
        new SijiaDatePickerFragment().show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.btn_regist_regist)
    public void regist(View view) {
        if (EditInputUtil.isEmpty(etUserName, etPassword, etPasswordCheck)) {
            return;
        }

        if (EditInputUtil.hasSpechars(etUserName)) {
            return;
        }

        String password = etPassword.getText().toString();
        String passwordCheck = etPasswordCheck.getText().toString();
        if (!password.equals(passwordCheck)) {
            etPasswordCheck.setError("两次密码不一致，请再次确认！");
            return;
        }
        if (password.length() < 8) {
            etPassword.setError("密码长度最少8位");
            return;
        }

        if (!NetUtil.isNetworkAvailable(this)) {
            toast("当前网络状态不良，请稍后再试");
            return;
        }

        boolean gender = true;
        if (rgGender.getCheckedRadioButtonId() == R.id.rbt_regist_female) {
            gender = false;
        }

        final SijiaUser sijiaUser = new SijiaUser();
        String name = etUserName.getText().toString();
        sijiaUser.setUsername(name);
        sijiaUser.setGender(gender);
        sijiaUser.setPassword(MD5.toHexString(password.getBytes()));
        sijiaUser.setBirthdate(tvBirthday.getText().toString());
        sijiaUser.setDeviceType("android");
        sijiaUser.setPinYinName(PinYinUtil.getPinYin(name));
        sijiaUser.setLetter(sijiaUser.getPinYinName().charAt(0));
        sijiaUser.setLocation(SijiaApplication.lastPosition);
        sijiaUser.setInstallId(BmobInstallation.getInstallationId(this));
        sijiaUser.setAllowFinded(true);
        sijiaUser.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                sijiaUser.login(RegistActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast("登录成功");
                        bmobUserManager.bindInstallationForRegister(sijiaUser.getUsername());
                        jumpTo(MainActivity.class, true);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toastAndLog("网络繁忙，请稍后登录", i, s);
                        jumpTo(MainActivity.class, true);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                if (i == 202) {
                    toast("该用户名已经存在");
                } else {
                    toastAndLog("网络繁忙", i, s);
                }
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        tvBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
