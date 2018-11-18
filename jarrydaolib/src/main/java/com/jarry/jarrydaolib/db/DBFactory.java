package com.jarry.jarrydaolib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

/**
 * 工厂类
 * Created by Jarry on 2018/9/2.
 */

public class DBFactory {
    static final String TAG = "DBFactory";
    private static DBFactory instance = new DBFactory();
    private static final String DB_NAME = "datas.db";//名字
    private static Context appContext;
    private SQLiteDatabase sqLiteDatabase;

    public static DBFactory getInstance(Context context) {
        appContext = context.getApplicationContext();
        return instance;
    }

    private DBFactory() {
    }

    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> baseDaoClazz, Class<M> entityClass) {

        File cacheDir = appContext.getCacheDir();
        Log.d(TAG, "cacheDir:" + cacheDir);
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(cacheDir + File.separator + DB_NAME, null);


        T baseDao = null;
        try {
            baseDao = baseDaoClazz.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;

    }

}
