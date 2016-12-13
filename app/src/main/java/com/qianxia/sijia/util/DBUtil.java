package com.qianxia.sijia.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.j256.ormlite.dao.Dao;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.entry.SijiaBitmap;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2016/11/5.
 */
public class DBUtil {
    private DBHelper dbHelper;
    private Dao<CityNameBean, String> daoCity;
    private Dao<MainCategoryTable, String> daoMainCategory;
    private Dao<SijiaBitmap, String> daoSijiaBitmap;
    private Dao<SubCategoryTable, String> daoSubCategory;

    public DBUtil(Context context) {
        dbHelper = DBHelper.getInstance(context);
        try {
            daoCity = dbHelper.getDao(CityNameBean.class);
            daoMainCategory = dbHelper.getDao(MainCategoryTable.class);
            daoSijiaBitmap = dbHelper.getDao(SijiaBitmap.class);
            daoSubCategory = dbHelper.getDao(SubCategoryTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCity(final CityNameBean cityNameBean) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (cityNameBean == null) {
                        return;
                    }
                    daoCity.createIfNotExists(cityNameBean);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void saveCities(final List<CityNameBean> list) {
        if (list != null && list.size() > 0) {
            try {
                daoCity.callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        for (CityNameBean bean : list) {
                            daoCity.createIfNotExists(bean);
                        }
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CityNameBean getCity(String cityName) {
        try {
            List<CityNameBean> list = daoCity.queryForEq("cityName", cityName);
            if (list != null && list.size() > 0) {
                CityNameBean cityNameBean = list.get(0);
                return cityNameBean;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("城市查询错误");
        }
    }

    public List<CityNameBean> getCities() {
        try {
            List<CityNameBean> list = daoCity.queryForAll();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("城市查询错误");
        }
    }

    public void saveMainCategories(final List<MainCategoryTable> mainCategoryTables) {
        try {
            daoMainCategory.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (MainCategoryTable mainCategoryTable : mainCategoryTables) {
                        daoMainCategory.createIfNotExists(mainCategoryTable);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MainCategoryTable> getMainCategories() {
        try {
            List<MainCategoryTable> list = daoMainCategory.queryForAll();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
//            throw new RuntimeException("主分类查询错误");
        }
        return null;
    }

    public void saveBitmap(final String imgUrl, final Bitmap bitmap) {
        new Thread() {
            @Override
            public void run() {
                SijiaBitmap sijiaBitmap = new SijiaBitmap();
                sijiaBitmap.setImgUrl(imgUrl);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                String bitmapStr = Base64.encodeToString(bytes, Base64.DEFAULT);
                sijiaBitmap.setBitmap(bitmapStr);
                try {
                    daoSijiaBitmap.createIfNotExists(sijiaBitmap);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getBitmap(final String imgUrl, final OnBitmapLoadedListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<SijiaBitmap> list = daoSijiaBitmap.queryForEq("imgUrl", imgUrl);
                    if (list != null && list.size() > 0) {
                        SijiaBitmap sijiaBitmap = list.get(0);
                        String bitmapStr = sijiaBitmap.getBitmap();
                        byte[] bytes = Base64.decode(bitmapStr, Base64.DEFAULT);
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onBitmapLoaded(imgUrl, bitmap);
                            }
                        });

                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onBitmapLoaded(imgUrl, null);
                            }
                        });
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("图像查询错误！");
                }
            }
        }.start();
    }

    public boolean isBitmapExist(String imgUrl) {
        try {
            List<SijiaBitmap> list = daoSijiaBitmap.queryForEq("imgUrl", imgUrl);
            if (list != null && list.size() > 0) {
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException("图像查询错误！");
        }
        return false;
    }

    public List<SubCategoryTable> getSubCategories(String mainCategoryId) {
        try {
            List<SubCategoryTable> list = daoSubCategory.queryForEq("mainCategoryId", mainCategoryId);
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveSubCategories(final List<SubCategoryTable> list) {
        if (list != null & list.size() > 0) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        daoSubCategory.callBatchTasks(new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                for (SubCategoryTable subCategoryTable : list) {
                                    daoSubCategory.createIfNotExists(subCategoryTable);
                                }
                                return null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void clearDataBase() {

    }
}
