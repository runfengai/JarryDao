package com.jarry.jarrydao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jarry.jarrydao.bean.Student;
import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 测试插入数据
     *
     * @param view
     */
    public void click(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        baseDao.insert(new Student(1, "jarry", 18));
    }
}
