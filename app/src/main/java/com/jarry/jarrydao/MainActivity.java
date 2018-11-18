package com.jarry.jarrydao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jarry.jarrydao.bean.Student;
import com.jarry.jarrydaolib.db.BaseDao;
import com.jarry.jarrydaolib.db.DBFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int index = 1;

    /**
     * 测试插入数据
     *
     * @param view
     */
    public void insertClick(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        baseDao.insert(new Student(index++, "jarry" + index, 20 + index));
    }

    /**
     * 查询操作
     *
     * @param view
     */
    public void query(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
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
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        Student updateStudent = new Student();
        updateStudent.age = 50;
        Student whereStu = new Student();
        whereStu.id = 1;

        long update = baseDao.update(updateStudent, whereStu);
        Log.d(TAG, "update=" + update);

    }

    public void delete(View view) {
        BaseDao<Student> baseDao = DBFactory.getInstance(this).getBaseDao(Student.class);
        Student updateStudent = new Student();
        updateStudent.age = 50;

        long delete = baseDao.delete(updateStudent);
        Log.d(TAG, "delete=" + delete);

    }
}
