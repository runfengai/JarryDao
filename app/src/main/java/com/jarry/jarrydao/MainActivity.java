package com.jarry.jarrydao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jarry.jarrydao.bean.Student;
import com.jarry.jarrydaolib.bean.Photo;
import com.jarry.jarrydaolib.bean.User;
import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;
import com.jarry.jarrydaolib.db.DaoImpl;
import com.jarry.jarrydaolib.db.DaoNewImpl;
import com.jarry.jarrydaolib.sub_sqlite.BaseDaoSubFactory;
import com.jarry.jarrydaolib.sub_sqlite.PhotoDao;
import com.jarry.jarrydaolib.sub_sqlite.UserDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    BaseDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = DBFactory.getInstance().getBaseDao(UserDao.class, User.class);
    }

    private int index = 1;

    /**
     * 测试插入数据
     *
     * @param view
     */
    public void insertClick(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance().getBaseDao(DaoImpl.class, Student.class);
        baseDao.insert(new Student((index++) % 10, "jarry" + index, 20 + index));
    }

    /**
     * 查询操作
     *
     * @param view
     */
    public void query(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance().getBaseDao(DaoImpl.class, Student.class);
        Student student = new Student();
//        student.age = 18;
//        student.name = "jarry";
        List<Student> studentList = baseDao.query(student);
        TextView viewById = (TextView) findViewById(R.id.text_res);
        viewById.setText("");
        for (Student student1 : studentList) {
            viewById.append(student1.id + " ");
            viewById.append(student1.name + " ");
            viewById.append(student1.age + " ");
        }
    }

    public void update(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance().getBaseDao(DaoImpl.class, Student.class);
        Student updateStudent = new Student();
        updateStudent.age = 50;
        Student whereStu = new Student();
        whereStu.id = 1;

        long update = baseDao.update(updateStudent, whereStu);
        Log.d(TAG, "update=" + update);

    }

    public void delete(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance().getBaseDao(DaoImpl.class, Student.class);
        Student updateStudent = new Student();
        updateStudent.age = 50;

        long delete = baseDao.delete(updateStudent);
        Log.d(TAG, "delete=" + delete);

    }

    /**
     * ===================================================================================================
     */

    int i = 0;

    public void loginClick(View view) {
        User user = new User();
        user.setId("No_" + (++i));
        user.setName("张三" + i);
        user.setPassword("123456");
//        long insert = userDao.insert(user);
        UserDao photoDao = BaseDaoSubFactory.getOurInstance().getSubDao(UserDao.class, User.class);
        long insert = photoDao.insert(user);
        Toast.makeText(this, "执行成功!" + insert, Toast.LENGTH_SHORT).show();
    }

    public void insertSub(View view) {
        Photo photo = new Photo();
        photo.setPath("data/data/aa.jpg");
        photo.setTime(new Date().toString());

        PhotoDao photoDao = BaseDaoSubFactory.getOurInstance().getSubDao(PhotoDao.class, Photo.class);
        photoDao.insert(photo);
        Toast.makeText(this, "执行成功!", Toast.LENGTH_SHORT).show();
    }

    public void insertCl(View view) {

    }

    public void updateClick(View view) {
        DaoNewImpl baseDao = DBFactory.getInstance().getBaseDao(DaoNewImpl.class, User.class);
        User user = new User();
        user.setName("abcd");
        User where = new User();
        where.setId("1");
        baseDao.update(user, where);
        Toast.makeText(this, "执行成功!", Toast.LENGTH_SHORT).show();
    }

    public void delClick(View view) {
        BaseDao baseDao = DBFactory.getInstance().getBaseDao(DaoNewImpl.class, User.class);
        User where = new User();
        where.setName("abcd");
        baseDao.delete(where);

    }

    public void selectClick(View view) {
        DaoNewImpl baseDao = DBFactory.getInstance().getBaseDao(DaoNewImpl.class, User.class);
        User where = new User();
        where.setName("abcd");
        List<User> list = baseDao.query(where);
        for (User user : list) {
            Log.d(TAG, user.getId());

        }
    }

}
