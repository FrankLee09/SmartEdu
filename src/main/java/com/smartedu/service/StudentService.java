package com.smartedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.exception.CustomException;
import com.smartedu.mapper.StudentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StudentService {
    @Resource
    StudentMapper studentMapper;


    public List<Student> getALlstudents(){
        return studentMapper.getAllstudents();
    }

    public List<Student> selectAll(Student student) {
        return studentMapper.selectAll(student);
    }

    public Student selectById(Long id){
        return studentMapper.selectById(id);
    };

    public PageInfo<Student> selectPage(Student student, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.selectAll(student);
        return PageInfo.of(list);
    }



    /** 新增班级，成功返回插入后带主键的对象 */
    public void addStudent(Student student){
        String id = String.valueOf(student.getId());

        student.setUsername(id);
        student.setPassword("123456");
        studentMapper.insert(student);
    }


    /** 更新班级信息，成功返回 true */
    public void updateStudent(Student student){
        studentMapper.update(student);
    };

    /** 根据 ID 删除班级，成功返回 true */
    public void deleteById(Long id){
        studentMapper.deleteById(id);
    };

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }

    public Student login(Account account) {
        String username = account.getUsername();
        Student dbstudent = studentMapper.selectByUsername(username);
        if (dbstudent == null) {
            throw new CustomException("500", "该账号不存在");
        }
        String password = account.getPassword();

        if (!dbstudent.getPassword().equals(password)) {
            throw new CustomException("500", "账号或密码错误");
        }
        return dbstudent;
    }

    public Student register(Student student) {

        this.addStudent(student);
        return studentMapper.selectByUsername(student.getUsername());
    }

    public void updatePassword(Account account) {
        Long id = account.getId();
        Student student = this.selectById(id);
        if (!student.getPassword().equals((account.getPassword()))) {
            throw new CustomException("500", "原密码输入错误，请输入正确密码");
        }
        student.setPassword(account.getNewPassword());
        this.updateStudent(student);
    }

}
