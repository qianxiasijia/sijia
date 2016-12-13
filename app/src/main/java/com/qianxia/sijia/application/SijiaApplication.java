package com.qianxia.sijia.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.ui.MainActivity;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SPUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatInstallation;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/10/2.
 */
public class SijiaApplication extends Application {
    public static Context context;
    public static BmobGeoPoint lastPosition; //Bmob的位置信息
    public static BDLocation lastLocation; //百度地图定位位置
    public static List<Activity> activities;
    public static CityNameBean selectedCity;
    public static File tempImgDir;
    public static DisplayImageOptions commOptionsCache;
    public static String localCity;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Bmob.initialize(this, Constant.BMOB_KEY);
        BmobInstallation.getCurrentInstallation(context).save();
        BmobPush.startWork(context);
        SDKInitializer.initialize(context);

        initImageLoader();

        activities = new ArrayList<Activity>();
        tempImgDir = new File(getFilesDir(), "tempImgDir");
        tempImgDir.mkdirs();

    }

    private void initImageLoader() {
//        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
//        Log.i("maxMemory",""+maxMemory);
//        LruCache memoryCache = new LruCache(maxMemory){
//            @Override
//            protected int sizeOf(Object key, Object value) {
//                return ((Bitmap)value).getByteCount()/1024;
//            }
//        };

        commOptionsCache = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_onloading)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(false)
                .displayer(new RoundedBitmapDisplayer(20))
                .displayer(new FadeInBitmapDisplayer(0)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCacheExtraOptions(720, 1080) // maxwidth, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(5)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new UsingFreqLimitedMemoryCache(16000000)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
//                .memoryCacheSize(maxMemory/8)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for releaseapp
                .build();//开始构建
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

    public static void updateSelectedCity(String cityName) {
        SPUtil spUtil = new SPUtil(context);
        DBUtil dbUtil = new DBUtil(context);
        spUtil.setFirstLogin(false);
        spUtil.setSelectedCity(cityName);
        SijiaApplication.selectedCity = dbUtil.getCity(cityName);
    }

    public static void logout() {
        BmobUserManager.getInstance(context).getCurrentUser().logOut(context);

        BmobQuery<BmobChatInstallation> query = new BmobQuery<>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        query.findObjects(context, new FindListener<BmobChatInstallation>() {
            @Override
            public void onSuccess(List<BmobChatInstallation> list) {
                if (list.size() > 0) {
                    BmobChatInstallation installation = list.get(0);
                    installation.setUid("");
                    installation.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            for (Activity act : activities) {
                                act.finish();
                            }

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(context, "网络繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                            Log.i("SIJIADEBUG", i + ":" + s);
                        }
                    });

                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "网络繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                Log.i("TAG", "错误代码" + i + ":" + s);
            }
        });
    }

    public static void showImageOnView(String imgUrl, final ImageView iv, int defaultImageResource) {
        final DBUtil dbUtil = new DBUtil(context);
        if (imgUrl == null) {
            iv.setImageResource(defaultImageResource);
            return;
        }
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
    }
}
