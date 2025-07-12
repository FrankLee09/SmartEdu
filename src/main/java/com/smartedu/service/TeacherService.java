package com.smartedu.service;

import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.entity.Teacher;
import com.smartedu.exception.CustomException;
import com.smartedu.mapper.TeacherMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    public List<Teacher> getAllTeachers() {
        return teacherMapper.getAllTeachers();
    }

    public List<Teacher> selectAll(Teacher teacher) {
        return teacherMapper.selectAll(teacher);
    }

    public Teacher selectById(Long id) {
        return teacherMapper.selectById(id);
    }

    public void insert(Teacher teacher) {
        teacherMapper.insert(teacher);
    }

    public void update(Teacher teacher) {
        teacherMapper.update(teacher);
    }

    public void deleteById(Long id) {
        teacherMapper.deleteById(id);
    }



    public Teacher login(Account account) {
        String username = account.getUsername();
        Teacher dbteacher = teacherMapper.selectByUsername(username);
        if (dbteacher == null) {
            throw new CustomException("500", "该账号不存在");
        }
        String password = account.getPassword();
        if (!dbteacher.getPassword().equals(password)) {
            throw new CustomException("500", "账号或密码错误");
        }
        return dbteacher;
    }


    public Teacher register(Teacher teacher) {
        this.insert(teacher);
        return teacherMapper.selectByUsername(teacher.getUsername());
    }

    public void updatePassword(Account account) {
        Long id = Long.valueOf(account.getId());
        Teacher teacher = this.selectById(id);
        if (!teacher.getPassword().equals((account.getPassword()))) {
            throw new CustomException("500", "原密码输入错误，请输入正确密码");
        }
        teacher.setPassword(account.getNewPassword());
        this.updatePassword(teacher);
    }
}
