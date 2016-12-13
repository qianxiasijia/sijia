package com.qianxia.sijia.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.entry.SijiaBitmap;
import com.qianxia.sijia.entry.SubCategoryTable;

import java.sql.SQLException;

/**
 * Created by Administrator on 2016/11/4.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static DBHelper dbHelper;

    private DBHelper(Context context) {
        super(context, "sijia.db", null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(context);
                }
            }
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, CityNameBean.class);
            TableUtils.createTableIfNotExists(connectionSource, SijiaBitmap.class);
            TableUtils.createTableIfNotExists(connectionSource, MainCategoryTable.class);
            TableUtils.createTableIfNotExists(connectionSource, SubCategoryTable.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, CityNameBean.class, true);
            TableUtils.dropTable(connectionSource, SijiaBitmap.class, true);
            TableUtils.dropTable(connectionSource, MainCategoryTable.class, true);
            TableUtils.dropTable(connectionSource, SubCategoryTable.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
