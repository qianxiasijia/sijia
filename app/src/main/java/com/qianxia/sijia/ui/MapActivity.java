package com.qianxia.sijia.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.manager.ShopManager;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.view.CircleImageView;

import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;


public class MapActivity extends BaseActivity {

    @Bind(R.id.mapview_map)
    MapView mapView;
    @Bind(R.id.ll_map_bottom)
    LinearLayout llMapBottom;
    @Bind(R.id.iv_map)
    CircleImageView ivMap;
    @Bind(R.id.tv_map_name)
    TextView tvName;
    @Bind(R.id.tv_map_addressorsignup)
    TextView tvAddressOrSign;
    @Bind(R.id.btn_map_more)
    Button btnMore;
    @Bind(R.id.rg_map_other)
    RadioGroup rgTop;

    private DBUtil dbUtil;
    private Shop shop;
    private LocationClient mClient;
    private BDLocationListener mListener;
    private BaiduMap baiduMap;
    private boolean isShowShopInfoWindow, isShowUserInfoWindow;
    private InfoWindow userInfo, shopInfo;
    private LatLng userLatLng, shopLatLng;
    private MarkerOptions shopOptions, userOptions;
    private SijiaUser intentUser;

    SijiaUser currentUser;
    private String from = "";
    private String keyword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_map);
    }

    @Override
    protected void init() {
        currentUser = bmobUserManager.getCurrentUser(SijiaUser.class);
        dbUtil = new DBUtil(this);
        baiduMap = mapView.getMap();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(17.0f));

//        baiduMap.setMaxAndMinZoomLevel(20,15);

        from = getIntent().getStringExtra("from");
        showUserLocation();
    }

    private void showBottomImage(String imgUrl) {
        if (dbUtil.isBitmapExist(imgUrl)) {
            dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                @Override
                public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                    ivMap.setImageBitmap(bitmap);
                }
            });
        } else {
            ImageLoader.getInstance().displayImage(imgUrl, ivMap, new ImageLoadingListener() {
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

    private void showNearyByFriends() {
        bmobUserManager.queryKiloMetersListByPage(false, 0, "location", SijiaApplication.lastPosition.getLongitude(),
                SijiaApplication.lastPosition.getLatitude(), true, 3.0, null, null, new FindListener<SijiaUser>() {
                    @Override
                    public void onSuccess(List<SijiaUser> list) {
                        if (list != null && list.size() > 0) {
                            showUserOnMap(list);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        toastAndLog("附件目前没有佳友", i, s);
                    }
                });

    }

    private void showUserOnMap(List<SijiaUser> list) {
        for (SijiaUser user : list) {
            if (user.isAllowFinded()) {
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(user.getLocation().getLatitude(), user.getLocation().getLongitude()));
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map));
                Marker marker = (Marker) baiduMap.addOverlay(options);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                marker.setExtraInfo(bundle);
            }
        }

        //移动地图中心点到当前登录用户位置(MyApp.lastPoint)
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(MyApp.lastPoint.getLatitude(), MyApp.lastPoint.getLongitude()));
//        baiduMap.animateMapStatus(msu);
//        //遍历附近的人
//        for(MyUser mu:users){
//            //设置Marker的参数
//            final MarkerOptions option = new MarkerOptions();
//            option.position(new LatLng(mu.getLocation().getLatitude(),mu.getLocation().getLongitude()));
//
//            //根据用户性别设置Marker使用什么样的图片
//            if(mu.getGender()){
//                option.icon(BitmapDescriptorFactory.fromResource(R.drawable.boy));
//            }else{
//                option.icon(BitmapDescriptorFactory.fromResource(R.drawable.girl));
//            }
//            //将Marker添加到地图上
//            Marker marker = (Marker) baiduMap.addOverlay(option );
//            //将用户的更多信息信息作为Marker的ExtraInfo添加到Marker中
//            //这些信息在Marker被点击的时候放到InfoWindow中呈现
//            Bundle bundle = new Bundle();
//
//            bundle.putString("username",mu.getUsername());
//            bundle.putString("avatar", mu.getAvatar());
//            bundle.putString("time", mu.getUpdatedAt());
//            bundle.putDouble("lat", mu.getLocation().getLatitude());
//            bundle.putDouble("lng", mu.getLocation().getLongitude());
//            bundle.putString("objectId", mu.getObjectId());
//
//            marker.setExtraInfo(bundle);

    }

    private void showUserLocation() {
        mClient = new LocationClient(this);
        mListener = new SijiaLocationListener();
        mClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000 * 60 * 5;//根据业务需求（定位间隔设置为5分钟）
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mClient.setLocOption(option);
        mClient.start();

    }

    private void showShopAddress() {
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    toast("网络繁忙,请稍后重试");
                } else {
                    shopLatLng = geoCodeResult.getLocation();
                    shopOptions = new MarkerOptions();
                    shopOptions.position(shopLatLng);
                    shopOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map));
                    baiduMap.addOverlay(shopOptions);
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(shopLatLng);
                    baiduMap.animateMapStatus(msu);

                    View view = getLayoutInflater().inflate(R.layout.inflate_infowindow_map_shop, null);
                    initShopInfoWindow(view);
                    shopInfo = new InfoWindow(view, shopLatLng, -60);
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        GeoCodeOption geoCoderOption = new GeoCodeOption();
        geoCoderOption.city(SijiaApplication.selectedCity.getCityName());
        geoCoderOption.address(shop.getAddress());
        geoCoder.geocode(geoCoderOption);
    }

    private void initShopInfoWindow(View view) {
        final ImageView iv = (ImageView) view.findViewById(R.id.iv_map_shopphoto);
        TextView tvName = (TextView) view.findViewById(R.id.tv_map_shopname);
        TextView tvAddress = (TextView) view.findViewById(R.id.tv_map_shopaddress);
        tvName.setText(shop.getName());
        tvAddress.setText(shop.getAddress());
        List<BmobFile> pics = shop.getPics();
        if (pics != null && pics.size() > 0) {
            String imgUrl = pics.get(0).getUrl();
            if (dbUtil.isBitmapExist(imgUrl)) {
                dbUtil.getBitmap(imgUrl, new OnBitmapLoadedListener() {
                    @Override
                    public void onBitmapLoaded(String imgUrl, Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }
                });
            } else {
                ImageLoader.getInstance().displayImage(imgUrl, iv, new ImageLoadingListener() {
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

        } else {
            iv.setImageResource(R.drawable.food_sample);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    private void refresh() {

        if (from.equals("food") || from.equals("shop")) {
            showShop();
        }

        if (from.equals("findfriend")) {
            showFriend();
        }

        if (from.equals("findother")) {
            showOther();
        }

        if (from.equals("findplace")) {
            showPlace();
        }
    }

    private void showPlace() {
        initToolbar("附近佳处");
        initFindFriendBottomView();
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(userLatLng);
        baiduMap.animateMapStatus(msu);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                shop = (Shop) bundle.getSerializable("shop");
                if (shop == null) {
                    initFindFriendBottomView();
                    return true;
                }
                tvName.setText(shop.getName());
                tvAddressOrSign.setText(shop.getAddress());
                ivMap.setVisibility(View.VISIBLE);
                String imgUrl = shop.getPics().get(0).getUrl();
                showBottomImage(imgUrl);
                btnMore.setVisibility(View.VISIBLE);
                btnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MapActivity.this, ShopDetailActivity.class);
                        intent.putExtra("shop", shop);
                        jumpTo(intent, false);
                    }
                });
                return true;
            }
        });
        ShopManager.getShopsBycity(this, SijiaApplication.selectedCity, new FindListener<Shop>() {
            @Override
            public void onSuccess(final List<Shop> list) {
                if (list != null && list.size() > 0) {

                    for (Shop shop : list) {
                        BmobGeoPoint point = shop.getLocationPoint();
                        if (point == null) {
                            getShopLatlngByAdress(shop);
                        } else {
                            shopLatLng = new LatLng(point.getLatitude(), point.getLongitude());
                            showPlaceOnMap(shop);
                        }
                    }
                } else {
                    toast("没有推荐的佳店");
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
            }
        });
    }

    private void initToolbar(String title) {

        toolbar.setTitle(title);
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

    private void showPlaceOnMap(final Shop shop) {
        double distance = DistanceUtil.getDistance(userLatLng, shopLatLng);
        log("distance:" + distance);
        if (distance >= 0 && distance <= 3000) {

            shopOptions = new MarkerOptions();
            shopOptions.position(shopLatLng);
            shopOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map));
            Marker mark = (Marker) baiduMap.addOverlay(shopOptions);
            Bundle bundle = new Bundle();
            bundle.putSerializable("shop", shop);
            mark.setExtraInfo(bundle);

        }
    }


    private void getShopLatlngByAdress(final Shop shop) {
        GeoCoder geocoder = GeoCoder.newInstance();
        geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {

                } else {
                    shopLatLng = geoCodeResult.getLocation();
                    showPlaceOnMap(shop);
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });

        GeoCodeOption option = new GeoCodeOption();
        option.city(shop.getCity().getCityName());
        option.address(shop.getAddress());
        geocoder.geocode(option);
    }

    private void showOther() {
        initToolbar("附近其他");
        initFindFriendBottomView();
        rgTop.setVisibility(View.VISIBLE);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(userLatLng);
        baiduMap.animateMapStatus(msu);
        rgTop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initFindFriendBottomView();
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                keyword = rb.getText().toString();
                showResult(keyword);
            }
        });

        rgTop.check(R.id.rb_map_food);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                if (bundle == null) {
                    return false;
                }
                tvName.setText(bundle.getString("name"));
                tvAddressOrSign.setText(bundle.getString("address"));
                return true;
            }
        });

    }

    private void showResult(String keyword) {
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    toast("没有找到相关的信息");
                } else {
                    List<PoiInfo> pois = poiResult.getAllPoi();
                    baiduMap.clear();
                    baiduMap.addOverlay(userOptions);
                    for (PoiInfo poi : pois) {
                        LatLng latlng = poi.location;
                        MarkerOptions option = new MarkerOptions();
                        option.position(latlng);
                        option.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map));
                        Marker marker = (Marker) baiduMap.addOverlay(option);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", poi.name);
                        bundle.putString("address", poi.address);
                        marker.setExtraInfo(bundle);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });

        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(keyword);
        option.radius(3000);
        option.location(userLatLng);
        option.pageCapacity(30);
        poiSearch.searchNearby(option);

    }

    private void showFriend() {
        initToolbar("附近佳友");
        initFindFriendBottomView();

        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(userLatLng);
        baiduMap.animateMapStatus(msu);

        showNearyByFriends();
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = marker.getExtraInfo();
                SijiaUser user = (SijiaUser) bundle.getSerializable("user");
                if (currentUser == null || currentUser.getObjectId().equals(user.getObjectId())) {
                    initFindFriendBottomView();
                    return true;
                }
                ivMap.setVisibility(View.VISIBLE);
                tvName.setText(user.getNick() == null ? user.getUsername() : user.getNick());
                tvAddressOrSign.setText(user.getSignature() == null ? "" : user.getSignature());
                String imgUrl = user.getAvatar();
                if (imgUrl != null) {
                    showBottomImage(imgUrl);
                } else {
                    ivMap.setImageResource(R.drawable.icon_my_photo);
                }
                btnMore.setVisibility(View.VISIBLE);
                intentUser = user;
                return true;
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, UserInfoActivity.class);
                intent.putExtra("user", intentUser);
                jumpTo(intent, false);
            }
        });
    }

    private void showShop() {
        initToolbar("佳店地址");
        shop = (Shop) getIntent().getSerializableExtra("shop");
        showShopAddress();
        btnMore.setVisibility(View.INVISIBLE);
        List<BmobFile> list = shop.getPics();
        if (list != null && list.size() > 0) {
            String imgUrl = shop.getPics().get(0).getUrl();
            showBottomImage(imgUrl);
        } else {
            ivMap.setImageResource(R.drawable.icon_my_photo);
        }
        tvName.setText(shop.getName());
        tvAddressOrSign.setText(shop.getAddress());

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                baiduMap.clear();
                if (marker.getPosition() == shopLatLng) {
                    isShowUserInfoWindow = false;
                    if (isShowShopInfoWindow) {
                        isShowShopInfoWindow = false;
                    } else {
                        isShowShopInfoWindow = true;
                        baiduMap.showInfoWindow(shopInfo);
                    }
                } else {
                    isShowShopInfoWindow = false;
                    if (isShowUserInfoWindow) {
                        isShowUserInfoWindow = false;
                    } else {
                        isShowUserInfoWindow = true;
                        baiduMap.showInfoWindow(userInfo);
                    }
                }
                baiduMap.addOverlay(shopOptions);
                baiduMap.addOverlay(userOptions);
                return false;
            }
        });
    }

    private void initFindFriendBottomView() {
        tvName.setText("我的位置");
        tvAddressOrSign.setText(SijiaApplication.lastLocation.getAddress().address);
        btnMore.setVisibility(View.INVISIBLE);
        ivMap.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClient.isStarted()) {
            mClient.unRegisterLocationListener(mListener);
            mClient.stop();
        }
        mapView.onDestroy();
    }

    private class SijiaLocationListener implements BDLocationListener {
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
                SijiaApplication.lastPosition = new BmobGeoPoint(lng, lat);

                String cityStr = bdLocation.getCity();
                SijiaApplication.localCity = cityStr.substring(0, cityStr.indexOf("市"));

                upCurrentUserLocation(null);
//                if(currentUser!=null){
//                    currentUser.setLocation(SijiaApplication.lastPosition);
//                    currentUser.update(MapActivity.this);
//                }
                userLatLng = new LatLng(lat, lng);
                userOptions = new MarkerOptions();
                userOptions.position(userLatLng);
                userOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_b));
                Marker marker = (Marker) baiduMap.addOverlay(userOptions);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", currentUser);
                marker.setExtraInfo(bundle);

                TextView tv = new TextView(MapActivity.this);
                tv.setTextSize(14);
                tv.setText("我的位置");
                tv.setPadding(16, 8, 16, 8);
                tv.setBackgroundResource(R.drawable.input_bg);
                userInfo = new InfoWindow(tv, userLatLng, -60);
                mClient.stop();

                refresh();
            } else {
                toast("定位失败，请稍后重试");
            }


//            int code = bdLocation.getLocType();
//            double lat = -1;
//            double lng = -1;
//
//            if(code == 61 || code==66 || code==161){
//                //定位成功了
//                lat = location.getLatitude();
//                lng = location.getLongitude();
//            }else{
//                //定位失败了
//                //则手动指定一个值(北京 潘家园)(我的策略)
//                lat = 39.876402;
//                lng = 116.465049;
//            }
//            mylocation = new LatLng(lat,lng);
//            MarkerOptions option = new MarkerOptions();
//            option.position(mylocation);
//            option.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
//            baiduMap.addOverlay(option );
//
//            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(mylocation);
//            baiduMap.animateMapStatus(msu);
//
//            TextView view = new TextView(FindActivity.this);
//
//            view.setText("我在这");
//            view.setTextColor(Color.WHITE);
//            view.setBackgroundColor(Color.RED);
//            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//            view.setPadding(5, 5, 5, 5);
//            InfoWindow infowindow = new InfoWindow(view , mylocation, -50);
//            baiduMap.showInfoWindow(infowindow);
//
//            //是否还需要重复定位？
//            //我的逻辑，显示成功后，就停止定位
//            if(mLocationClient.isStarted()){
//                mLocationClient.unRegisterLocationListener(myListener);
//                mLocationClient.stop();
//            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
