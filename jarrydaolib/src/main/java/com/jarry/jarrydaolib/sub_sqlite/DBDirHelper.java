package com.jarry.jarrydaolib.sub_sqlite;

import com.jarry.jarrydaolib.bean.User;
import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;

import java.io.File;

/**
 * 私有数据库帮助类，生成对应的数据库
 * Created by Jarry on 2018/11/18.
 */
public enum DBDirHelper {
    database("");
    private String value;

    DBDirHelper(String value) {

    }

    public String getValue() {
        UserDao userDao = DBFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File("data/data/com.jarry.jarrydao");
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/" + currentUser.getId() + "_login.db";
            }

        }
        return null;
    }

}
