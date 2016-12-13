package com.qianxia.sijia.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.listener.OnLoadedAllCitiesListener;
import com.qianxia.sijia.manager.CityManager;
import com.qianxia.sijia.util.CheckDataBaseUtil;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.PermissionUtils;
import com.qianxia.sijia.util.SPUtil;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener {

    @Bind(R.id.iv_splash_welcom)
    ImageView ivWelcom;

    private SPUtil spUtil;
    private DBUtil dbUtil;
    private LocationClient mLocationClient;
    private BDLocationListener locationListener;
    private boolean hasReceiveLocation;
    private boolean isAnimationFinished;
    //    private int locationCount;
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {
                startApp();
            }
        }
    };
    private boolean isExit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void init() {
        spUtil = new SPUtil(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < 23) {
            startApp();
        } else {
            PermissionUtils.requestMultiPermissions(SplashActivity.this, mPermissionGrant);
        }
    }

    private void startApp() {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        ivWelcom.startAnimation(animation);

        CheckDataBaseUtil.checkAndCreateDataBase(this);
        dbUtil = new DBUtil(this);
        SijiaApplication.selectedCity = dbUtil.getCity(spUtil.getSelectedCity());
        startLocation();
//        CityManager.getCities(this, new OnLoadedAllCitiesListener() {
//            @Override
//            public void onLoadedAllCities(List<CityNameBean> list) {
//                if (list!=null&&list.size()>0) {
//                    startLocation();
//                } else {
//                    toast("网络繁忙，请稍后重试");
//                }
//            }
//        });
    }

    private void startLocation() {
        mLocationClient = new LocationClient(this);
        locationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(locationListener);
        LocationClientOption locationOptions = new LocationClientOption();
        locationOptions.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOptions.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 60 * 1;
        locationOptions.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        locationOptions.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        locationOptions.setOpenGps(true);//可选，默认false,设置是否使用gps
        locationOptions.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        locationOptions.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOptions.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOptions.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOptions.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOptions.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(locationOptions);
        mLocationClient.start();
        log("定位启动");
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isAnimationFinished = true;
        if (hasReceiveLocation) {
            if (!spUtil.isFirstUse()) {
                jumpTo(MainActivity.class, true);
            } else {
                jumpTo(CitySelectActivity.class, true);
            }
            log("跳转启动");
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            int code = bdLocation.getLocType();
            log("code:" + code);
            double lat = -1;
            double lng = -1;
            if (code == 61 || code == 66 || code == 161) {
                lat = bdLocation.getLatitude();
                lng = bdLocation.getLongitude();
                SijiaApplication.lastLocation = bdLocation;
                log(SijiaApplication.lastLocation.toString());
                SijiaApplication.lastPosition = new BmobGeoPoint(lng, lat);

                String cityStr = bdLocation.getCity();
                String cityName = cityStr.substring(0, cityStr.indexOf("市"));
//                spUtil.setCurrentCity(cityName);
//                dbUtil = new DBUtil(SplashActivity.this);
                SijiaApplication.localCity = cityName;
//                SijiaApplication.selectedCity=dbUtil.getCity(spUtil.getCurrentCity());
                upCurrentUserLocation(null);
//                toastAndLog("定位成功",code,"定位成功");


            } else {
//                toastAndLog("定位失败",code,"定位失败");
//                SijiaApplication.selectedCity=dbUtil.getCity(spUtil.getCurrentCity());
            }
            mLocationClient.unRegisterLocationListener(locationListener);
            mLocationClient.stop();
            hasReceiveLocation = true;
            if (isAnimationFinished) {
                if (!spUtil.isFirstUse()) {
                    jumpTo(MainActivity.class, true);
                } else {
                    jumpTo(CitySelectActivity.class, true);
                }
                log("跳转启动");
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(locationListener);
            mLocationClient.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
