package com.jarry.jarrydaolib.sub_sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;

/**
 * Created by Jarry on 2018/11/18.
 */
public class BaseDaoSubFactory extends DBFactory {

    private static final BaseDaoSubFactory instance = new BaseDaoSubFactory();

    //定义一个用于实现分库的数据库操作对象
    protected SQLiteDatabase subSqliteDatabase;

    public static BaseDaoSubFactory getOurInstance() {
        return instance;
    }

    protected BaseDaoSubFactory() {

    }

    public <T extends BaseDao<M>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(DBDirHelper.database.getValue()) != null) {
            return (T) map.get(DBDirHelper.database.getValue());
        }
        subSqliteDatabase = SQLiteDatabase.openOrCreateDatabase(DBDirHelper.database.getValue(), null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSqliteDatabase, entityClass);
            map.put(DBDirHelper.database.getValue(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;
    }
}
