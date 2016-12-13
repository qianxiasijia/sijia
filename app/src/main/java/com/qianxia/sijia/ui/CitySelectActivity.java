package com.qianxia.sijia.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
//import com.qianxia.sijia.utils.HttpUtil;
import com.qianxia.sijia.manager.CityManager;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SPUtil;
import com.qianxia.sijia.view.SijiaTextView;

import butterknife.Bind;

//import com.android.volley.Response;
//import com.qianxia.sijia.utils.CityUtil;

public class CitySelectActivity extends BaseActivity {

    private DBUtil dbUtil;

    @Bind(R.id.tv_cityselect_localcity)
    TextView tvLocalCity;

    private SijiaTextView tvSearch;
    private boolean isExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_cityselect);
    }

    @Override
    protected void init() {
        dbUtil = new DBUtil(this);
        initToolbar();
        initView();

    }

    private void initToolbar() {
        toolbar.setTitle("思佳");
        toolbar.setSubtitle("城市选择");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbUtil.getCity("北京") == null) {
            CityManager.getCities(this, null);
        }
    }

    private void initView() {
        if (SijiaApplication.lastLocation != null) {
            String localCity = SijiaApplication.lastLocation.getCity();
            if (localCity != null) {
                tvLocalCity.setText(localCity.substring(0, localCity.indexOf("市")));
            } else {
                tvLocalCity.setText("定位失败");
            }
        } else {
            tvLocalCity.setText("定位失败");
        }


        tvSearch = (SijiaTextView) findViewById(R.id.tv_cityselect_search);
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitySelectActivity.this, CitySearchActivity.class);
                startActivityForResult(intent, Constant.REQUESTCODE_CITYSEARCH);
                overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

            }
        });

    }

    public void onClick(View view) {
        String cityName = ((TextView) view).getText().toString();
        if ("定位失败".equals(cityName)) {
            cityName = "北京";
        }
        SijiaApplication.updateSelectedCity(cityName);
        jumpTo(MainActivity.class, true);
    }


//
//    private void loadDatas(String foodName) {
//
//        new HttpUtil().loadDatas(foodName,"北京");
//        Log.i("TAG:loadDatas", "北京 end");
//
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil().loadDatas(foodName,"上海");
//        Log.i("TAG:loadDatas", "上海 end");
//
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil().loadDatas(foodName,"深圳");
//        Log.i("TAG:loadDatas", "深圳 end");
//
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil().loadDatas(foodName,"广州");
//        Log.i("TAG:loadDatas", "广州 end");
//
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil().loadDatas(foodName,"烟台");
//        Log.i("TAG:loadDatas", "烟台 end");
//
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        new HttpUtil().loadDatas(foodName,"合肥");
//        Log.i("TAG:loadDatas", "合肥 end");
//
//    }
//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Constant.RESULT_CITYSELECTED) {
//            String cityStr = data.getStringExtra("city");
            jumpTo(MainActivity.class, true);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//
//    @Override
//    public void citySelected(CityNameBean city) {
//        jumpTo(MainActivity.class,true);
//    }

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            toast("再点击一次确定退出程序");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 5000);
        } else {
            super.onBackPressed();
        }
    }
}
