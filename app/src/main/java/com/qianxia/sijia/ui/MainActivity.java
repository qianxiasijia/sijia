package com.qianxia.sijia.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.SijiaFragmentPagerAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.listener.OnCitySelectedListener;
import com.qianxia.sijia.listener.OnFragmentSwitchListener;
import com.qianxia.sijia.manager.SijiaFragmentManager;
import com.qianxia.sijia.receiver.SiJiaReceiver;
import com.qianxia.sijia.view.CircleImageView;
import com.qianxia.sijia.view.SijiaBottomView;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

public class MainActivity extends BaseActivity implements OnFragmentSwitchListener, EventListener {

    @Bind(R.id.viewpager_main_main)
    ViewPager viewPager;
    @Bind(R.id.bv_food_main)
    SijiaBottomView bvFood;
    @Bind(R.id.bv_share_main)
    SijiaBottomView bvShare;
    @Bind(R.id.bv_nearby_main)
    SijiaBottomView bvNearby;
    @Bind(R.id.bv_my_main)
    SijiaBottomView bvMy;
    @Bind(R.id.drawerlayout_main)
    DrawerLayout mainDrawerLayout;
    @Bind(R.id.navigation_view_main_drawer)
    NavigationView navigationView;


    protected TextView btnCity;

    private List<Fragment> fragments;
    private SijiaFragmentPagerAdapter adapter;
    private SijiaBottomView[] bottomViews;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean isExit;
    private View headerView;
    private TextView tvNickName;
    private TextView tvUserName;
    private CircleImageView ivAvatar;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void init() {
//        log("mainfragment:"+String.valueOf(bmobUserManager.getCurrentUser(SijiaUser.class).isAllowFinded()));
        SiJiaReceiver.regist(this);

        initDrawerLayout();
        initBottomView();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeardView();
    }

    public void setToolbarTextViewVisibility(boolean flag) {
        btnCity = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        if (flag) {
            btnCity.setVisibility(View.VISIBLE);
        } else {
            btnCity.setVisibility(View.INVISIBLE);
        }
//        btnCity.setText(SijiaApplication.selectedCity.getCityName());
//        btnCity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                citySelect();
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initDrawerLayout() {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (bmobUserManager.getCurrentUser() == null) {
                    toast("你还没有登录呢");
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.item_exit_menu_drawer_main:
                        SijiaApplication.logout();
                        mainDrawerLayout.closeDrawers();
                        break;
                    case R.id.item_setting_menu_drawer_main:
                        jumpTo(SettingActivity.class, false);
                        mainDrawerLayout.closeDrawers();
                        break;
                    case R.id.item_favorite_menu_drawer_main:
                        jumpTo(CollectActivity.class, false);
                        mainDrawerLayout.closeDrawers();
                        break;
                    case R.id.item_mypost_menu_drawer_main:
                        jumpTo(PostListActivity.class, false);
                        mainDrawerLayout.closeDrawers();
                        break;
                }

                return true;
            }
        });
        headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3, true);
                mainDrawerLayout.closeDrawers();
            }
        });

        tvNickName = (TextView) headerView.findViewById(R.id.tv_count_nickname);
        tvUserName = (TextView) headerView.findViewById(R.id.tv_count_username);
        ivAvatar = (CircleImageView) headerView.findViewById(R.id.iv_count_atavar);
        refreshHeardView();
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mainDrawerLayout, toolbar, R.string.drawer_toggle_open, R.string.drawer_toggle_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mainDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void refreshHeardView() {
        SijiaUser currentuser = bmobUserManager.getCurrentUser(SijiaUser.class);
        if (currentuser != null) {
            String nickName = currentuser.getNick();
            if (TextUtils.isEmpty(nickName)) {
                tvNickName.setText("昵称：  ");
            } else {
                tvNickName.setText("昵称：  " + nickName);
            }
            tvUserName.setText("用户名：  " + currentuser.getUsername());
            String url = currentuser.getAvatar();
            SijiaApplication.showImageOnView(url, ivAvatar, R.drawable.icon_my_photo);
        } else {
            tvUserName.setText("点击登录");
            ivAvatar.setImageResource(R.drawable.icon_my_photo);
            tvNickName.setText("");
        }
    }


    private void initBottomView() {
        bottomViews = new SijiaBottomView[4];
        bottomViews[0] = bvFood;
        bottomViews[1] = bvShare;
        bottomViews[2] = bvNearby;
        bottomViews[3] = bvMy;

        for (SijiaBottomView bottomView : bottomViews) {
            bottomView.setPaintAlpha(0);
        }
        bottomViews[0].setPaintAlpha(255);

    }

    private void initViewPager() {
        fragments = SijiaFragmentManager.getFragmentList(this);
        FragmentManager fm = getSupportFragmentManager();
        adapter = new SijiaFragmentPagerAdapter(fm, fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position < bottomViews.length - 1) {
                    bottomViews[position].setPaintAlpha((int) (255 - 255 * positionOffset));
                    bottomViews[position + 1].setPaintAlpha((int) (255 * positionOffset));
                }

            }

            @Override
            public void onPageSelected(int position) {
                for (SijiaBottomView bottomView : bottomViews) {
                    bottomView.setPaintAlpha(0);
                }
                bottomViews[position].setPaintAlpha(255);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.bv_food_main, R.id.bv_share_main, R.id.bv_nearby_main, R.id.bv_my_main})
    public void setCurrentFragment(View view) {
        switch (view.getId()) {
            case R.id.bv_food_main:

                viewPager.setCurrentItem(0, false);
                break;
            case R.id.bv_share_main:

                viewPager.setCurrentItem(1, false);
                break;
            case R.id.bv_nearby_main:

                viewPager.setCurrentItem(2, false);
                break;
            case R.id.bv_my_main:

                viewPager.setCurrentItem(3, false);
                break;
        }
    }

    @Override
    public void fragmentSwitch(Constant.EnumFragment fragment, Fragment newFragment) {
        int position = -1;
        switch (fragment) {
            case FRAGMENT_FOOD:
                position = 0;
                break;
            case FRAGMENT_SHARE:
                position = 1;
                break;
            case FRAGMENT_NEARBY:
                position = 2;
                break;
            case FRAGMENT_MY:
                position = 3;
        }
        if (position != -1) {
            adapter.replaceFragment(position, newFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_toolbar_search) {
            jumpTo(SearchActivity.class, false);
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void citySelected(CityNameBean city) {
//        btnCity.setText(city.getCityName());
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        log("resultCode:"+resultCode);
//        if (resultCode==Constant.RESULT_CITYSELECTED) {
//            String cityStr = data.getStringExtra("city");
//            log("cityStr:"+cityStr);
//            btnCity.setText(cityStr);
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
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

    @Override
    public void onMessage(BmobMsg message) {

    }

    @Override
    public void onReaded(String conversionId, String msgTime) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {

    }

    @Override
    public void onAddUser(BmobInvitation message) {

    }

    @Override
    public void onOffline() {
        SijiaApplication.logout();
        toast("您的账号已经在其他设备上登录，请注意账号安全");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SiJiaReceiver.unRegist(this);

    }

    //    public void citySelect(){
//        Intent intent = new Intent(this,CitySearchActivity.class);
//        startActivityForResult(intent,Constant.REQUESTCODE_CITYSEARCH);
////        if(Build.VERSION.SDK_INT<23){
////            showCitySelectDialogFragment();
//            return;
//        }
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)!=PackageManager.PERMISSION_GRANTED||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
//            if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
//                    ||!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                    showMessageOKCancel("需要授权建立本地数据库，才能使用此功能", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(BaseActivity.this,new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},Constant.REQUESTCODE_DATABASE);
//                            return;
//                        }
//                    });
//            }
//                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},Constant.REQUESTCODE_DATABASE);
//        } else {
////            showCitySelectDialogFragment();
////        }
//
//    }
}
