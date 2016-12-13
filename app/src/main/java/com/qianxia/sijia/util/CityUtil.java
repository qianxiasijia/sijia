//package com.qianxia.sijia.utils;
//
//import android.util.Log;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.gson.Gson;
//import com.qianxia.sijia.application.SijiaApplication;
//import com.qianxia.sijia.entry.City;
//import com.qianxia.sijia.entry.DistrictesBean;
//import com.qianxia.sijia.entry.Region;
//
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.digest.DigestUtils;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//import cn.bmob.v3.BmobBatch;
//import cn.bmob.v3.BmobObject;
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.datatype.BatchResult;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//import cn.bmob.v3.listener.QueryListListener;
//import cn.bmob.v3.listener.SaveListener;
//
//
///**
// * Created by tarena on 2016/10/11.
// */
//public class CityUtil {
//
//    private static RequestQueue queue = Volley.newRequestQueue(SijiaApplication.context);
//    private static final String APPKEY="49814079";
//    private static final String APPSECRET="90e3438a41d646848033b6b9d461ed54";
//
//    ThreadPoolExecutor tpe = new ThreadPoolExecutor(1,Integer.MAX_VALUE,0, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
//    ThreadPoolExecutor tpe1 = new ThreadPoolExecutor(1,Integer.MAX_VALUE,0,TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>());
//    Semaphore sem = new Semaphore(1);
//    Semaphore sem1 = new Semaphore(1);
//
//    int districtNum=0;
//
//
//    Response.Listener listener = new Response.Listener<String>() {
//        @Override
//        public void onResponse(String s) {
//            Gson gson = new Gson();
//            DistrictesBean bean = gson.fromJson(s, DistrictesBean.class);
//            final List<BmobObject> cities = new ArrayList<BmobObject>();
//            final List<DistrictesBean.City> temp = bean.getCities();
//
//
//            for (int i = 1142;i<temp.size();i++) {
//                final DistrictesBean.City city = temp.get(i);
//                final int tempCityId = i+1;
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            sem.acquire();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        final int num = tempCityId;
//                        final String cityName = city.getCity_name();
//                        Log.i("城市数量：", num + "");
//                        if (cityName == "全国") {
//                            sem.release();
//                            return;
//                        }
//                        final List<DistrictesBean.City.District> districts = city.getDistricts();
////
//                        if (districts == null || districts.size() == 0) {
//                            sem.release();
//                            return;
//                        }
//                        Log.i("cityName",cityName);
//
//                        BmobQuery<City> query = new BmobQuery<City>();
//                        query.addWhereEqualTo("cityName",cityName);
//                        query.findObjects(new FindListener<City>() {
//                            @Override
//                            public void done(List<City> list, BmobException e) {
//                                if (e == null && list.size() > 0) {
//
//                                    final City city1 = list.get(0);
//                                    Log.i("city1",city1.getCityName());
//                                    districtNum=0;
//                                    for (final DistrictesBean.City.District district : districts) {
//                                        Runnable runnable3 = new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    sem1.acquire();
//                                                } catch (InterruptedException e1) {
//                                                    e1.printStackTrace();
//                                                }
//                                                final int temp = ++districtNum;
//                                                Region region = new Region();
//                                                String districtName = district.getDistrict_name();
//                                                region.setName(districtName);
//                                                region.addAllUnique("neighborhoods", district.getNeighborhoods());
//                                                region.setCity(city1);
//                                                region.save(new SaveListener<String>() {
//                                                    @Override
//                                                    public void done(String s, BmobException e) {
//                                                        sem1.release();
//                                                        Log.i("sem1的剩余数量",sem1.availablePermits()+"城市编号:"+num);
//
//                                                        if(temp==districts.size()){
//                                                            Log.i("temp", "temp="+temp+",size"+districts.size());
//                                                            sem.release();
//                                                            Log.i("sem",sem.availablePermits()+"");
//
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        };
//
//                                        tpe1.execute(runnable3);
//                                    }
//
//                                } else {
//                                    sem.release();
//                                    Log.i("sem",sem.availablePermits()+"");
//                                }
//
//
//
//                            }
//                        });
//                    }
//                };
//
//                tpe.execute(runnable);
//            }
//
//
//
////                saveCities(cities);
//
//        }
//
//        private void saveRegions(final List<Region> regions, final Semaphore sem) {
//
//
//            final int size = regions.size();
//
//            if(size<=50){
//                new BmobBatch().insertBatch(new ArrayList<BmobObject>(regions)).doBatch(new QueryListListener<BatchResult>() {
//                    @Override
//                    public void done(List<BatchResult> list, BmobException e) {
//
//                        if(e==null){
//                            Log.i("上传成功","上传成功"+regions.size());
//                        }else{
//                            Log.i("上传失败","上传失败"+regions.size());
//                        }
//                        sem.release();
//                        return;
//                    }
//                });
//            }
//
//            int rem = (size + 1) % 50;
//
//            Log.i("rem", rem + "");
//
//            List<BmobObject> lasts = new ArrayList<BmobObject>(regions.subList(regions.size() - rem, regions.size() - 1));
//
//            for (int i = 0; i < regions.size(); i++) {
//
//                if ((i + 1) % 50 == 0) {
//                    final List<BmobObject> tempRegions = new ArrayList<BmobObject>(regions.subList(i - 49, i));
//
//                    new BmobBatch().insertBatch(tempRegions).doBatch(new QueryListListener<BatchResult>() {
//                        @Override
//                        public void done(List<BatchResult> list, BmobException e) {
//                            if (e == null) {
//                                Log.i("上传成功","上传成功"+regions.size());
//
//                            } else {
//                                Log.i("上传失败","上传失败"+regions.size());
//                            }
//                        }
//                    });
//                }
//            }
//
//            new BmobBatch().insertBatch(lasts).doBatch(new QueryListListener<BatchResult>() {
//                @Override
//                public void done(List<BatchResult> list, BmobException e) {
//                    if (e == null) {
//                        Log.i("上传成功","上传成功"+regions.size());
//                    } else {
//                        Log.i("上传失败","上传失败"+regions.size());
//                    }
//                    sem.release();
//                }
//            });
//
//        }
//    };
//
//
//
//    public static void getCities(Response.Listener<String> listener){
//        Map<String,String> params = new HashMap<>();
//
//        String url= getUrl("http://api.dianping.com/v1/metadata/get_regions_with_businesses", params);
//        StringRequest request = new StringRequest(url, listener,null);
//        queue.add(request);
//    }
//
//
//
//    /**
//     * 符合大众点评要求的URL
//     *
//     */
//
//    public static String getUrl(String url, Map<String,String> params){
//        String result="";
//
//        String sign = getSign(APPKEY,APPSECRET,params);
//        String query = getQuery(APPKEY,sign,params);
//        result = url+"?"+query;
//
//        return result;
//    }
//    /**
//     * 构造符合大众点评要求的完整访问参数
//     *
//     */
//
//    private static String getQuery(String appKey, String sign, Map<String, String> params) {
//
//        try {
//            // 添加签名
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("appkey=").append(appKey).append("&sign=").append(sign);
//            for (Entry<String, String> entry : params.entrySet()){
//                stringBuilder.append('&').append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(),"utf8"));
//            }
//            String queryString = stringBuilder.toString();
//
//            return queryString;
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            throw new RuntimeException("不正确的编码格式"); //异常也可以作为一个方法的返回值
//        }
//    }
//
//
//    /**
//     *
//     * 获得签名
//
//     * @param params
//     * @return
//     */
//
//    private static String getSign(String appKey, String appSecret, Map<String, String> params) {
//
//        StringBuilder stringBuilder = new StringBuilder();
//
//        // 对参数名进行字典排序
//        String[] keyArray = params.keySet().toArray(new String[0]);
//        Arrays.sort(keyArray);
//        // 拼接有序的参数名-值串
//        stringBuilder.append(appKey);
//        for (String key : keyArray) {
//            stringBuilder.append(key).append(params.get(key));
//        }
//        String codes = stringBuilder.append(appSecret).toString();
////		String sign = org.apache.commons.codec.digest.DigestUtils.shaHex(codes).toUpperCase();
//        String sign = new String(Hex.encodeHex(DigestUtils.sha(codes))).toUpperCase();
//        return sign;
//    }
//
//
//    private void saveCities(List<BmobObject> cities) {
//        int size = cities.size();
//        int rem = (size+1)%50;
//
//        Log.i("rem",rem+"");
//
//        List<BmobObject> lasts = new ArrayList<BmobObject>(cities.subList(cities.size()-rem,cities.size()-1));
//
//        new BmobBatch().insertBatch(lasts).doBatch(new QueryListListener<BatchResult>() {
//            @Override
//            public void done(List<BatchResult> list, BmobException e) {
//                if(e==null){
//                    for(int j=0;j<list.size();j++){
//                        BatchResult result = list.get(j);
//                        BmobException ex =result.getError();
//                        if(ex==null){
//                            Log.i("TAG","第"+j+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                        }else{
//                            Log.i("TAG","第"+j+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                        }
//                    }
//                }else{
//                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                }
//            }
//        });
//
//
//        ExecutorService es = Executors.newFixedThreadPool(2);
//        final Semaphore semaphore = new Semaphore(2);
//
//        for(int i = 0;i<cities.size();i++){
//
//            if((i+1)%50==0){
//                final List<BmobObject> tempCities = new ArrayList<BmobObject>(cities.subList(i-49,i));
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            semaphore.acquire();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        new BmobBatch().insertBatch(tempCities).doBatch(new QueryListListener<BatchResult>() {
//                            @Override
//                            public void done(List<BatchResult> list, BmobException e) {
//                                if(e==null){
//                                    for(int j=0;j<list.size();j++){
//                                        BatchResult result = list.get(j);
//                                        BmobException ex =result.getError();
//                                        if(ex==null){
//                                            Log.i("TAG",semaphore.availablePermits()+"第"+j+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                                        }else{
//                                            Log.i("TAG",semaphore.availablePermits()+"第"+j+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                                        }
//                                    }
//                                }else{
//                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                                }
//                            }
//                        });
//                        semaphore.release();
//                    }
//                };
//                es.execute(runnable);
//            }
//        }
//        es.shutdown();
//    }
//
//
//
//}
