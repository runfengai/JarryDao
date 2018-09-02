package com.jarry.jarrydaolib.db;

/**
 * 数据库操作接口
 * Created by Jarry on 2018/9/2.
 */

public interface IBaseDao<T> {
    long insert(T bean);
}
