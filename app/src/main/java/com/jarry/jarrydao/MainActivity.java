package com.jarry.jarrydao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jarry.jarrydao.bean.Student;
import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;

import java.util.List;

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
    public void insertClick(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        baseDao.insert(new Student(1, "jarry", 18));
    }

    /**
     * 查询操作
     *
     * @param view
     */
    public void query(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        Student student = new Student();
        student.age = 18;
        List<Student> studentList = baseDao.query(student);
        TextView viewById = (TextView) findViewById(R.id.text_res);
        for (Student student1 : studentList) {
            viewById.append(student1.id + " ");
            viewById.append(student1.name + " ");
            viewById.append(student1.age + " ");
        }
    }
}
