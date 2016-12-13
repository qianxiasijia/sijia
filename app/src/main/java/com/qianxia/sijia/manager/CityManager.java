package com.qianxia.sijia.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.listener.OnLoadedAllCitiesListener;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.NetUtil;
import com.qianxia.sijia.util.PinYinUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/9/17.
 */
public class CityManager {

    private static int count = 0;
    private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private static Semaphore sem = new Semaphore(1);

    /**
     * 获取全部城市列表数据
     *
     * @param context  上下文对象
     * @param listener 城市列表全部加载完成后回调监听
     */
    public static void getCities(final Context context, final OnLoadedAllCitiesListener listener) {
        final List<CityNameBean> cityNameBeans = new ArrayList<>();
        count = 0;

        final DBUtil dbUtil = new DBUtil(context);
        List<CityNameBean> list = dbUtil.getCities();
        if (list != null && list.size() > 0) {
            listener.onLoadedAllCities(list);
            return;
        }

        if (!NetUtil.isNetworkAvailable(context)) {
            Toast.makeText(context, "网络不良，请稍后重试！", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < 4; i++) {
            final int temp = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BmobQuery<City> query = new BmobQuery<>();
                    query.setLimit(1000);
                    query.setSkip(temp * 1000);
                    query.findObjects(context, new FindListener<City>() {
                        @Override
                        public void onSuccess(List<City> list) {
                            sem.release();
                            if (list.size() > 0) {
                                count++;
//                                Log.i("TAG:list", list.toString());
                                for (City city : list) {
                                    String name = city.getCityName();
                                    if (name.equals("全国")) {
                                        continue;
                                    }
                                    String pinYinName = PinYinUtil.getPinYin(name).toUpperCase();
                                    CityNameBean cityNameBean = new CityNameBean();
                                    cityNameBean.setCityId(city.getObjectId());
                                    cityNameBean.setCityName(name);
                                    cityNameBean.setPyName(pinYinName);
                                    cityNameBean.setLetter(pinYinName.charAt(0));
                                    cityNameBeans.add(cityNameBean);
                                }
                                if (count == 3) {
                                    sortCity(cityNameBeans);
                                    dbUtil.saveCities(cityNameBeans);
                                    listener.onLoadedAllCities(cityNameBeans);
                                }
                            } else {
                                Log.i("TAG", "获取城市列表失败，请稍候再试！");
                                return;

                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "网络繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                }
            };
            tpe.execute(runnable);
        }
    }

    private static void sortCity(List<CityNameBean> cityNameBeans) {
        Collections.sort(cityNameBeans, new Comparator<CityNameBean>() {
            @Override
            public int compare(CityNameBean lhs, CityNameBean rhs) {
                String l = lhs.getPyName();
                String r = rhs.getPyName();
                if (l.charAt(0) == r.charAt(0)) {
                    return lhs.getCityName().compareTo(rhs.getCityName());
                } else {
                    return lhs.getPyName().toUpperCase().compareTo(rhs.getPyName().toUpperCase());
                }
            }
        });
    }

    public static List<CityNameBean> getSearchCity(List<CityNameBean> datas, String name) {
        List<CityNameBean> cityNameBeanList = new ArrayList<>();

        if (name.matches("[\u4e00-\u9fff]{1,}")) {
            //输入中文
            for (CityNameBean cb : datas) {
                if (cb.getCityName().contains(name)) {
                    cityNameBeanList.add(cb);
                }
            }
        } else {
            //输入拼音
            for (CityNameBean cb : datas) {
                if (cb.getPyName().contains(name)) {
                    cityNameBeanList.add(cb);
                }
            }
        }

        return cityNameBeanList;
    }
}
