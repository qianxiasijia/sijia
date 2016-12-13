//package com.qianxia.sijia.utils;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Environment;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.qianxia.sijia.application.SijiaApplication;
//import com.qianxia.sijia.entry.City;
//import com.qianxia.sijia.entry.Food;
//import com.qianxia.sijia.entry.Shop;
//import com.qianxia.sijia.entry.SubCategory;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.datatype.BmobFile;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
////import cn.bmob.v3.listener.QueryListener;
//import cn.bmob.v3.listener.SaveListener;
//import cn.bmob.v3.listener.UploadFileListener;
//
//public class HttpUtil {
//    public static final String ROOT="http://www.dianping.com";
//    public static String ROOT_SEARCH;
//    public static File fileDir;
//    public static String mKeyword;
//    public static ArrayBlockingQueue<Runnable> workQueue=new ArrayBlockingQueue<>(100);
//    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(20,30,1, TimeUnit.SECONDS,workQueue);
//    public String queryCityName;
//
//    public void loadDatas(final String keyword,final String cityName){
//        switch (cityName){
//            case "北京": ROOT_SEARCH="http://www.dianping.com/search/keyword/2/0_"; break;
//            case "上海": ROOT_SEARCH="http://www.dianping.com/search/keyword/1/0_"; break;
//            case "深圳": ROOT_SEARCH="http://www.dianping.com/search/keyword/7/0_"; break;
//            case "烟台": ROOT_SEARCH="http://www.dianping.com/search/keyword/148/0_"; break;
//            case "广州": ROOT_SEARCH="http://www.dianping.com/search/keyword/4/0_"; break;
//            case "合肥": ROOT_SEARCH="http://www.dianping.com/search/keyword/110/0_"; break;
//        }
//        queryCityName = cityName;
//        mKeyword = keyword;
//        deleteAllFilesInSdcard();
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<String> shopIds = getShopIdsByKeyword(keyword);
//                if(shopIds==null){
//                    return;
//                }
//                loadShopsDatasById(shopIds,keyword);
//            }
//        });
//
//    }
//
//
//
//    //获取商店信息
//    private  void loadShopsDatasById(ArrayList<String> shopIds, final String keyword) {
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        for(String str:shopIds){
//
//            final String shopNum =  str.substring(str.lastIndexOf("/")+1,str.length());
//            Log.i("TAG:load",shopNum);
//            String urlStr = ROOT+str;
//            try {
//                Connection connection = Jsoup.connect(urlStr);
//                connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//                final Document doc = connection.get();
//                String tempName =null;
//                if(doc.select("h1.shop-name")!=null&&doc.select("h1.shop-name").first()!=null){
//                    tempName = doc.select("h1.shop-name").first().ownText();
//                    Log.i("TAG:load",tempName);
//                }else{
//                    continue;
//                }
//                final String shopName =tempName;
//                final String openTime= doc.select("p.info").select("p.info-indent").select("p:not(.J_license)").first().select("span").text();
//                Log.i("TAG:load",openTime);
//                final String address = doc.select("div.expand-info").select("div.address").text();
//                Log.i("TAG:load",address);
//                final String tel = doc.select("p.expand-info").select("p.tel").text();
//                Log.i("TAG:load",tel);
//                final String scriptStr = doc.select("script").toString();
//                String foodStr = getFoodHtml(scriptStr, keyword);
//
//                if(foodStr==null){
//                    continue;
//                }
//
//                final String foodUrl = ROOT + foodStr;
//                BmobQuery<Shop> qe1 = new BmobQuery<>();
//                BmobQuery<Shop> qe2 = new BmobQuery<>();
//                qe1.addWhereEqualTo("name",shopName);
//                qe2.addWhereEqualTo("address",address);
//                try {
//                    Thread.sleep(3000);
//                    Log.i("TAG:Thread","ThreadSleep");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                List<BmobQuery<Shop>> querise = new ArrayList<>();
//                querise.add(qe1);
//                querise.add(qe2);
//                BmobQuery<Shop> query = new BmobQuery<>();
//                query.and(querise);
//                query.findObjects(new FindListener<Shop>() {
//                    Shop shop = null;
//                    @Override
//                    public void done(List<Shop> list, BmobException e) {
//                        Log.i("TAG:QueryShop",String.valueOf(e==null));
//                        if (e==null) {
//                            shop = list.get(0);
//                            Log.i("TAG:shop",shop.getName());
//                            getFoodInfobyHtml(foodUrl, shopNum, shop);
//
//                        } else {
//                            shop = new Shop();
//                            String shopDescription = getShopDescription(scriptStr);
//                            if (shopDescription == null&&doc.select("p.desc.J-desc").first()!=null) {
//                                shopDescription = doc.select("p.desc.J-desc").first().text();
//                            }else{
//                                shopDescription="暂无信息";
//                            }
//                            Log.i("TAG:shopDescription", shopDescription);
//                            shop.setAddress(address);
//                            shop.setName(shopName);
//                            shop.setDescription(openTime + "\n" + tel + "\n" + shopDescription);
//                            shop.setLikerNum((new Random().nextInt(20)) + 80);
//                            shop.setRating(new Random().nextInt(3) * 0.5f + 5.5f);
//                            //获得商店照片
//                            executor.execute(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getShopPhotos(scriptStr, shopNum, shop,foodUrl);
//                                }
//                            });
//                        }
//                    }
//                });
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }
//
//
//    private  String getFoodHtml(String scriptStr,String keyword) {
//        String shopUrl = null;
//        StringBuilder builder = new StringBuilder();
//        for (int i=0;i<keyword.length();i++){
//            builder.append(keyword.charAt(i)).append("+").append("\\S*");
//        }
//        String regex = "/shop.*dish-\\S*"+builder+"(?=\")";
//        Log.i("TAG:regex",regex);
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(scriptStr);
//        if(matcher.find()){
//            shopUrl = matcher.group();
//        }
//        return shopUrl;
//    }
//
//    private  void getFoodInfobyHtml(final String foodStr, String shopNum, final Shop shop) {
//        final String foodName = foodStr.substring(foodStr.lastIndexOf("-") + 1, foodStr.length());
//        Log.i("TAG:foodName", foodName);
//        BmobQuery<Food> query1 = new BmobQuery<>();
//        BmobQuery<Food> query2 = new BmobQuery<>();
//        query1.addWhereEqualTo("name", foodName);
//        query2.addWhereEqualTo("shop", shop);
//        Log.i("TAG:queryFood", shop.getObjectId()+shop.getName());
//        List<BmobQuery<Food>> queries1 = new ArrayList<BmobQuery<Food>>();
//        queries1.add(query1);
//        queries1.add(query2);
//        BmobQuery<Food> mainQuery = new BmobQuery<Food>();
//        mainQuery.and(queries1);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//
//        }
//        //TODO
//        mainQuery.findObjects(new FindListener<Food>() {
//            @Override
//            public void done(List<Food> list, BmobException e) {
//                if (e == null) {
//                    Log.i("QueryFood", String.valueOf(e == null));
//                    Log.i("QueryFood", list.get(0).getShop().getName());
//
//                } else {
//                    try {
//                        String encode = URLEncoder.encode(foodName, "utf8");
//                        final String foodUrl = foodStr.replace(foodName, encode);
//                        Log.i("TAG:food", foodUrl);
//                        executor.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    getFoodInfo(foodUrl, foodName, shop);
//                                } catch (IOException e1) {
//                                    e1.printStackTrace();
//                                }
//                            }
//                        });
//
//                    } catch (IOException ex) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private void getFoodInfo(String foodUrl, String foodName, final Shop shop) throws IOException {
//        Connection connection = Jsoup.connect(foodUrl);
//        connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//        final Document doc = connection.get();
//        String dishPrice = doc.select("span.dish-price").text();
//        Log.i("TAG:foodInfo", dishPrice);
//
//        final Food food = new Food();
//        Elements elements = doc.select("div.comment-txt div.J_brief-cont");
//        String foodDescription = null;
//        if (elements != null&&elements.first()!=null) {
//            foodDescription = elements.first().text();
//        } else {
//            foodDescription = "暂无信息";
//        }
//        if(!TextUtils.isEmpty(dishPrice)){
//            Pattern pattern = Pattern.compile("\\d+");
//            Matcher matcher = pattern.matcher(dishPrice);
//            matcher.find();
//            String foodPrice = matcher.group();
//            food.setPrice(Float.parseFloat(foodPrice));
//        }else{
//            food.setPrice(0.0f);
//        }
//        Log.i("TAG:foodDescription", foodDescription);
//
//        food.setDescription(foodDescription);
//        food.setName(foodName);
//        food.setLikerNum((new Random().nextInt(20)) + 80);
//        food.setRating(new Random().nextInt(3) * 0.5f + 5.5f);
//        food.setShop(shop);
//
//        BmobQuery<SubCategory> query = new BmobQuery<>();
//        query.addWhereEqualTo("categoryName", mKeyword);
//        query.findObjects(new FindListener<SubCategory>() {
//            @Override
//            public void done(List<SubCategory> list, BmobException e) {
//                if(e==null){
//                    food.setSubCategory(list.get(0));
//                    BmobQuery<City> query1 = new BmobQuery<>();
//                    query1.addWhereEqualTo("cityName", queryCityName);
//                    query1.findObjects(new FindListener<City>() {
//                        @Override
//                        public void done(List<City> list, BmobException e) {
//                            if(e==null){
//                                food.setCity(list.get(0));
//                                executor.execute(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        getFoodPhotos(doc, shop, food);
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//                }
//
//            }
//        });
//    }
//
//    private  void getFoodPhotos(Document doc, final Shop shop, final Food food) {
////          Elements elements = doc.select("li.s-item-3 img[src]");
//        final Elements elements = doc.select("li.s-item-3 a[href]");
//        int i =0;
//        String sdcard = SijiaApplication.context.getExternalFilesDir(null).getAbsolutePath();
//        File fooddir = new File(sdcard,shop.getObjectId());
//        if(!fooddir.exists()){
//            fooddir.mkdirs();
//        }
//        final List<BmobFile> photos = new ArrayList<>();
//        for (Element element: elements) {
//            String photoHtml =ROOT+element.attr("href");
//            String photoUrl = getPhotoUrlfromHtml(photoHtml);
//            if(photoUrl==null){
//                continue;
//            }
//            File photoFile = new File(fooddir,"photo"+i+".jpg");
//            final int count=++i;
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            try {
//                URL url = new URL(photoUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("SijiaUser-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//                int statusCode = connection.getResponseCode();
//                if(statusCode==200){
//                    InputStream in = connection.getInputStream();
//
//                    OutputStream out = new FileOutputStream(photoFile);
//                    Log.i("TAG:load",photoFile.getAbsolutePath());
//                    Bitmap bitmap = compressBitmap(in);
////                    Bitmap bitmap = BitmapFactory.decodeStream(in);
//                    Log.i("TAG:load",String.valueOf(bitmap==null));
//
//                    boolean flag=bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
//                    Log.i("TAG:load",photoFile.getName()+" "+String.valueOf(flag));
//                    out.flush();
//                    out.close();
//                    final BmobFile bmobFile = new BmobFile(photoFile);
//                    bmobFile.uploadblock(new UploadFileListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if(e==null) {
//                                photos.add(bmobFile);
//                                if(count>4||count==elements.size()){
//                                    food.addAllUnique("pics",photos);
//                                    food.save(new SaveListener<String>() {
//                                        @Override
//                                        public void done(String s, BmobException e) {
//                                          if(e==null){
//                                              Log.i("TAG:上传美食图片",s);
//                                          }else{
//                                              Log.i("TAG:SaveFood","false");
//                                          }
//                                        }
//                                    });
//                                }
//
//                            }
//                            else{
//                                Log.i("图片上传失败",e.getMessage());
//                            }
//                        }
//                    });
//
//
//                }else{
//                    System.out.println("连接失败"+statusCode);
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(i>4){
//                return;
//            }
//        }
//
//    }
//
//    private  void getShopPhotos(String scriptStr,String shopNum,Shop shop,String foodUrl) {
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_UNMOUNTED.equals(state)|Environment.MEDIA_REMOVED.equals(state)){
//            throw new RuntimeException("SD卡不存在");
//        }
//        String sdcard = SijiaApplication.context.getExternalFilesDir(null).getAbsolutePath();
//
//        //获得商店照片集的字符串
//        Pattern pattern = Pattern.compile("<div class=\"container clearfix\">[\\s\\S]*tag-环境");
//        Matcher matcher=pattern.matcher(scriptStr);
//
//        if(matcher.find()){
//            //获得每一张照片的html
//            Log.i("TAG","找到商店照片集的字符串");
//            String PhotoStr=matcher.group();
//            Pattern patternHref = Pattern.compile("href=\"(\\S*)\"");
//            Matcher matcherHref = patternHref.matcher(PhotoStr);
//
//
//            loadImgToSdcard(shopNum, matcherHref, sdcard,shop,foodUrl);
//        }else {
//            Log.i("TAG",shop.getName()+"没有找到商店照片集的字符串");
//        }
//
//    }
//
//    private void loadImgToSdcard(String shopNum, Matcher matcherHref, String sdcard,Shop shop,String foodUrl) {
//        fileDir = new File(sdcard+"/"+shopNum);
//        if(!fileDir.exists()){
//            fileDir.mkdirs();
//        }
//        int i =0;
//        List<BmobFile> photos = new ArrayList<>();
//        while (matcherHref.find()){
//            //从html中获得照片的url
//            File file = new File(fileDir,shopNum+i+".png");
//            String photoHtml = ROOT+matcherHref.group(1);
//          String photoUrl = getPhotoUrlfromHtml(photoHtml);
//            if(photoUrl==null){
//                continue;
//            }
//            Log.i("TAG:ShopPhotoUrl",photoUrl);
//            i++;
//            loadPhoto(file, photoUrl,i,shop,photos,foodUrl,shopNum);
//            if(i>4){
//                return;
//            }
//        }
//        Log.i("TAG:loadImgToSdcard",shop.getName()+"没有找到商店照片url");
//
//    }
//
//    private  void loadPhoto(File file, String photoUrl, final int count, final Shop shop, final List<BmobFile> photos, final String foodUrl, final String shopNum) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            URL url = new URL(photoUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("SijiaUser-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//            int statusCode = connection.getResponseCode();
//            if(statusCode==200){
//                InputStream in = connection.getInputStream();
//
//                OutputStream out = new FileOutputStream(file);
//                Log.i("TAG:ShopPhotoPath",file.getAbsolutePath());
//                 Bitmap bitmap = compressBitmap(in);
////                    Bitmap bitmap = BitmapFactory.decodeStream(in);
////                Log.i("TAG:load",String.valueOf(bitmap==null));
//
//                boolean flag=bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
//                Log.i("TAG:ShopPhoto",file.getName()+" "+String.valueOf(flag));
//                out.flush();
//                out.close();
//                final BmobFile bmobFile = new BmobFile(file);
//                bmobFile.uploadblock(new UploadFileListener() {
//                    @Override
//                    public void done(BmobException e) {
//                        if(e==null) {
//                            photos.add(bmobFile);
//                            if(count>4){
//
//                                BmobQuery<City> query = new BmobQuery<>();
//                                query.addWhereEqualTo("cityName",queryCityName);
//                                query.findObjects(new FindListener<City>() {
//                                    @Override
//                                    public void done(List<City> list, BmobException e) {
//                                        if(e==null){
//                                            shop.setCity(list.get(0));
//                                            shop.addAllUnique("pics",photos);
////                                            Log.i("TAG:setCitySuccess",list.get(0).getCityName());
//                                            shop.save(new SaveListener<String>() {
//                                                @Override
//                                                public void done(String s, BmobException e) {
//                                                    if(e==null){
//                                                        Log.i("TAG:上传商店信息",s);
//                                                        BmobQuery<Shop> bmobQuery = new BmobQuery<Shop>();
//                                                        bmobQuery.getObject(s, new QueryListener<Shop>() {
//                                                            @Override
//                                                            public void done(final Shop shop, BmobException e) {
//                                                                if(e==null){
//                                                                    getFoodInfobyHtml(foodUrl, shopNum, shop);
//                                                                }
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            });
//                                        }else{
//
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                        else{
//                            Log.i("图片上传失败",e.getMessage());
//                        }
//                    }
//                });
//
//
//            }else{
//                System.out.println("连接失败"+statusCode);
//            }
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private  Bitmap compressBitmap(InputStream in) {
//        Bitmap bitmap = null;
//        byte[] bytes = getBytesFromStream(in);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds=true;
//        BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
//        int width = options.outWidth;
//        int height = options.outHeight;
//        int requestSize=150;
//        int scale = 1;
//        while(true){
//            if(width/2<requestSize||height/2<requestSize){
//                break;
//            }
//            width/=2;
//            height/=2;
//            scale*=2;
//        }
//        options.inSampleSize=scale;
//        options.inJustDecodeBounds=false;
//        bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
//        return bitmap;
//    }
//
//    private  byte[] getBytesFromStream(InputStream in) {
//        byte[] datas = null;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        try {
//            int len=0;
//            while ((len=in.read(buffer))!=-1){
//                out.write(buffer,0,len);
//            }
//            datas = out.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(in!=null){
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(out!=null){
//                try {
//                    out.flush();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return datas;
//    }
//
//    private void deleteAllFilesInSdcard() {
//        String state = Environment.getExternalStorageState();
//        if(Environment.MEDIA_UNMOUNTED.equals(state)|Environment.MEDIA_REMOVED.equals(state)){
//            throw new RuntimeException("SD卡不存在");
//        }
//        String sdcard = SijiaApplication.context.getExternalFilesDir(null).getAbsolutePath();
//        File sdcardFile = new File(sdcard);
//        if(sdcardFile.exists()||sdcardFile.isDirectory()){
//            File[] files=sdcardFile.listFiles();
//            if(files.length==0){
//                return;
//            }
//            deletFiles(files);
//        }
//    }
//
//    private void deletFiles(File[] files) {
//        for(File file:files){
//           if(file.isDirectory()&&file.listFiles().length>0){
//              File[] childFiles = file.listFiles();
//               deletFiles(childFiles);
//           }else{
//               file.delete();
//           }
//        }
//    }
//
//    private  String getPhotoUrlfromHtml(String photoHtml) {
//        Connection connection= Jsoup.connect(photoHtml);
//        connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//        try {
//            Document doc=connection.get();
//            String photoUrl = doc.select("img#J_main-img").first().attr("src");
//            return  photoUrl;
//        } catch (IOException e) {
//            return null;
//        }
//
//    }
//
//    private  String getShopDescription(String scriptStr) {
//        String shopDescription = null;
//        Pattern pattern = Pattern.compile("<p class=\"J_all Hide\">(.*)</p>");
//
//        Matcher matcher = pattern.matcher(scriptStr);
//        if (matcher.find()) {
//            shopDescription = matcher.group(1);
//        }
//
//        return shopDescription;
//    }
//
//    public  ArrayList<String> getShopIdsByKeyword(String keyword){
//        ArrayList<String> shopIds = new ArrayList<>();
//        try {
//            String str = URLEncoder.encode(keyword,"utf8");
//            String urlStr = ROOT_SEARCH+str+"/o2";
//            Connection connection = Jsoup.connect(urlStr);
//            connection.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:49.0) Gecko/20100101 Firefox/49.0");
//            connection.header("Referer",ROOT_SEARCH+str);
//            Document document = connection.get();
//            Elements elements = document.select("div.pic a[href]");
//            for(Element element:elements){
//                shopIds.add(element.attr("href"));
//            }
//            return shopIds;
//
//        } catch (UnsupportedEncodingException e) {
//            return null;
//        } catch (IOException e) {
//            return null;
//        }
//    }
//
//}
