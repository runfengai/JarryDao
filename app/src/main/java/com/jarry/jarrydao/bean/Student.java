package com.jarry.jarrydao.bean;

import com.jarry.jarrydaolib.annotation.DBField;
import com.jarry.jarrydaolib.annotation.DBTable;

/**
 * Created by Jarry on 2018/9/2.
 */
@DBTable("t_student")
public class Student {
    @DBField("_id")
    public int id;
    @DBField("_name")
    public String name;
    @DBField("_age")
    public int age;

    public Student() {
    }

    public Student(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
