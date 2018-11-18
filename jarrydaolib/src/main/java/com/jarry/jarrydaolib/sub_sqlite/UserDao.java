package com.jarry.jarrydaolib.sub_sqlite;

import com.jarry.jarrydaolib.bean.User;
import com.jarry.jarrydaolib.db.BaseDao;

import java.util.List;

/**
 * Created by Jarry on 2018/11/18.
 */
public class UserDao extends BaseDao<User> {
    @Override
    public long insert(User bean) {
        List<User> query = query(bean);
        User where = null;
        for (User user : query) {
            where = new User();
            where.setId(user.getId());
            user.setStatus(0);

            update(user, where);
        }
        //用户登录
        bean.setStatus(1);
        return super.insert(bean);
    }

    public User getCurrentUser() {
        User user = new User();
        user.setStatus(1);
        List<User> query = query(user);
        if (query.size() > 0) {
            return query.get(0);
        }
        return null;
    }
}
