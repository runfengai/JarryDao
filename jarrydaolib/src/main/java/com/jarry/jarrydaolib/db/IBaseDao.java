package com.jarry.jarrydaolib.db;

import java.util.List;

/**
 * 数据库操作接口
 * Created by Jarry on 2018/9/2.
 */

public interface IBaseDao<T> {
    long insert(T bean);
    List<T> query(String where);
}
