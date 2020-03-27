package com.rookie.service;

import com.rookie.pojo.Stu;

// 测试
public interface StuService {

    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);
}
