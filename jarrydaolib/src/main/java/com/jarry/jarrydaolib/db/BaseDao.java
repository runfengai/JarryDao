package com.jarry.jarrydaolib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.jarry.jarrydaolib.annotation.DBField;
import com.jarry.jarrydaolib.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 实现类
 * Created by Jarry on 2018/9/2.
 */

public class BaseDao<T> implements IBaseDao<T> {
    static final String TAG = "BaseDao";
    private SQLiteDatabase sqLiteDatabase;

    private String tableName;

    private Class<T> entityClass;
    private boolean isInit = false;
    private HashMap<String, Field> cachedHashMap;//缓存数据库字段-参数对应关系

    protected boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;

        if (!isInit) {
            DBTable annotation = entityClass.getAnnotation(DBTable.class);
            if (annotation == null)
                tableName = entityClass.getSimpleName();
            else
                tableName = annotation.value();

            String createSql = getCreateSql();
            sqLiteDatabase.execSQL(createSql);
            cachedHashMap = new HashMap<>();
            initCacheMap();

            isInit = true;
        }

        return isInit;
    }

    /**
     * 缓存k-v
     */
    private void initCacheMap() {
        //取列名
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
        }
        for (String columnName : columnNames) {
            Field columnField = null;
            for (Field declaredField : declaredFields) {
                String fieldName = "";
                DBField annotation = declaredField.getAnnotation(DBField.class);
                if (annotation != null) {
                    fieldName = annotation.value();
                } else fieldName = declaredField.getName();

                if (columnName.equals(fieldName)) {
                    columnField = declaredField;
                    break;
                }
            }

            if (columnField != null) {
                cachedHashMap.put(columnName, columnField);
            }
        }


    }

    /**
     * 建表语句
     * create table a ( id integer, name text, sno varchar(20))
     *
     * @return
     */
    private String getCreateSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName).append(" ( ");
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {//手动拼装
            Class<?> type = field.getType();//参数的类型
            String name = type.getCanonicalName();
            Log.d(TAG, "参数类型:" + name);
            //拼装参数
            DBField dbField = field.getAnnotation(DBField.class);
            if (dbField == null) {//如果不存在，则尝试取参数直接当做表字段
                if (type == String.class) {
                    stringBuilder.append(field.getName() + " TEXT,");
                } else if (type == Integer.class || name.equals("int")) {
                    stringBuilder.append(field.getName() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuilder.append(field.getName() + "BIGINT,");
                } else if (type == Double.class) {
                    stringBuilder.append(field.getName() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuilder.append(field.getName() + " BLOB,");
                } else {
                    Log.d(TAG, "type：" + type + " 类型不支持");
                    continue;
                }
            } else {
                if (type == String.class) {
                    stringBuilder.append(dbField.value() + " TEXT,");
                } else if (type == Integer.class || name.equals("int")) {
                    stringBuilder.append(dbField.value() + " INTEGER,");
                } else if (type == Long.class) {
                    stringBuilder.append(dbField.value() + " BIGINT,");
                } else if (type == Double.class) {
                    stringBuilder.append(dbField.value() + " DOUBLE,");
                } else if (type == byte[].class) {
                    stringBuilder.append(dbField.value() + " BLOB,");
                } else {
                    Log.d(TAG, "type：" + type + " 类型不支持");
                    continue;
                }
            }
        }
        int len = stringBuilder.length();
        if (stringBuilder.charAt(len - 1) == ',') {
            stringBuilder.deleteCharAt(len - 1);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }


    @Override
    public long insert(T bean) {

        //根据bean 获取到表参数-表内容的map
        Map<String, String> map = getValues(bean);
        ContentValues contentValues = getContentValues(map);
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set<String> strings = map.keySet();
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = map.get(key);
            if (value != null)
                contentValues.put(key, value);
        }
        return contentValues;
    }

    /**
     * @param bean
     * @return
     */
    private Map<String, String> getValues(T bean) {
        Map<String, String> map = new HashMap<>();
        Iterator<Field> iterator = cachedHashMap.values().iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.setAccessible(true);
            try {
                //获取成员变量的值
                Object object = field.get(bean);
                if (object == null)
                    continue;
                String value = object.toString();
                String key = null;
                DBField dbField = field.getAnnotation(DBField.class);
                if (dbField != null)
                    key = dbField.value();
                else key = field.getName();

                if (!TextUtils.isEmpty(key) && value != null) {
                    map.put(key, value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }
}
