package com.smartedu.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.smartedu.common.Result;
import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.entity.Teacher;
import com.smartedu.exception.CustomException;
import com.smartedu.service.StudentService;
import com.smartedu.service.TaskService;
import com.smartedu.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class WebController {

    @Resource
    private StudentService studentService;
    @Resource
    private TeacherService teacherService;
 
    

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account result = null;
        if ("学生".equals(account.getRole())) {
            result = studentService.login(account);
        } else if ("教师".equals(account.getRole())) {
            result = teacherService.login(account);
        } else {
            throw new CustomException("500", "非法输入");
        }
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Student student) {
        Student dbStudent = studentService.register(student);
        return Result.success(dbStudent);
    }

    @PostMapping("/teacherRegister")
    public Result teacherRegister(@RequestBody Teacher teacher) {
        teacher.setRole("教师");
        Teacher dbTeacher = teacherService.register(teacher);
        return Result.success(dbTeacher);
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("教师".equals(account.getRole())) {
            teacherService.updatePassword(account);
        } else if ("学生".equals(account.getRole())) {
            studentService.updatePassword(account);
        }
        return Result.success();
    }
}
