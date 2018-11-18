package com.jarry.jarrydaolib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.jarry.jarrydaolib.annotation.DBField;
import com.jarry.jarrydaolib.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    @Override
    public long delete(T where) {
        Map<String, String> values = getValues(where);
        Condition condition = new Condition(values);
        return sqLiteDatabase.delete(tableName, condition.whereCause, condition.whereArgs);
    }

    @Override
    public long update(T entity, T where) {
        long res = -1;
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);

        //条件语句， "id = ?" , new String[]{"123"}
        Map whereMap = getValues(where);

        //获取对应的Condition,用于拼装最后的执行语句
        Condition condition = new Condition(whereMap);

        res = sqLiteDatabase.update(tableName, contentValues, condition.whereCause, condition.whereArgs);


        return res;
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String groupBy, String having,
                         String orderBy) {
        List<T> list = new ArrayList<>();

        //根据where对象获取查询条件，<K,V>形式
        Map<String, String> map = getValues(where);
        //根据map拼装selection和selectionArgs
        StringBuilder selectionSB = new StringBuilder();
        Set<String> keySet = map.keySet();
        String[] selectionArgs = keySet.size() > 0 ? new String[keySet.size()] : null;
        selectionSB.append(" 1=1 ");
        Iterator<String> iterator = keySet.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            selectionSB.append(" and ")
                    .append(next)
                    .append(" = ")
                    .append(" ? ")
                    .append(" ");
            selectionArgs[index++] = map.get(next);
        }
//        selectionSB = selectionSB.replace(selectionSB.length() - " and ".length(), selectionSB.length(), "");
        String selection = selectionSB.toString();

        Cursor cursor = sqLiteDatabase.query(tableName, null, selection, selectionArgs, null, null, null);
        Object obj = null;
        while (cursor.moveToNext()) {
            try {
                obj = where.getClass().newInstance();//
                Iterator<Map.Entry<String, Field>> iterator1 = cachedHashMap.entrySet().iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<String, Field> next = iterator1.next();
                    String columnName = next.getKey();
                    Integer columnIndex = cursor.getColumnIndex(columnName);
                    Field value = next.getValue();
                    Class<?> type = value.getType();//数据类型
                    String name = type.getCanonicalName();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            value.set(obj, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            value.set(obj, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class || name.equals("int")) {
                            value.set(obj, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            value.set(obj, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            value.set(obj, cursor.getBlob(columnIndex));
                        } else {
                            continue;
                        }
                    }
                }
                list.add((T) obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return list;
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
     * 根据泛型实体，查询到对应的参数名称（无注解时以成员变量名称代替）-内容的键值对
     *
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

    /**
     * 拼装条件语句的
     */
    private class Condition {
        public String whereCause;
        private String[] whereArgs;


        public Condition(Map<String, String> where) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
            Set set = where.keySet();
            int size = set.size();
            //where参数拼装
            ArrayList<String> whereArg = new ArrayList<>();

            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = where.get(key);
                //拼装字符串
                stringBuilder.append(" and " + key + "= ?");
                whereArg.add(value);
            }
            this.whereCause = stringBuilder.toString();
            this.whereArgs = whereArg.toArray(new String[size]);

        }


    }
}
