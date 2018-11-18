package com.jarry.jarrydaolib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 工厂类
 * Created by Jarry on 2018/9/2.
 */

public class DBFactory {
    static final String TAG = "DBFactory";
    private static DBFactory instance = new DBFactory();
    private static final String DB_NAME = "datas.db";//名字
    private SQLiteDatabase sqLiteDatabase;

    private String sqliteDateBasePath;
    //    数据库连接池
    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    public static DBFactory getInstance() {
        return instance;
    }

    protected DBFactory() {
        sqliteDateBasePath = "data/data/com.jarry.jarrydao/jarry.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDateBasePath, null);
    }

    public <T extends BaseDao<M>, M> T getBaseDao(Class<T> baseDaoClazz, Class<M> entityClass) {
        if (map.get(baseDaoClazz.getCanonicalName()) != null) {
            return (T) map.get(baseDaoClazz.getCanonicalName());
        }

        T baseDao = null;
        try {
            baseDao = baseDaoClazz.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
            map.put(baseDaoClazz.getCanonicalName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;

    }

}
