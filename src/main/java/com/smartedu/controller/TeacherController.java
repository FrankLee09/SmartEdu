package com.smartedu.controller;

import com.smartedu.common.Result;
import com.smartedu.entity.Course;
import com.smartedu.entity.Teacher;
import com.smartedu.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    // 查询所有
    @GetMapping("/getAll")
    public Result getAll() {
        List<Teacher> list = teacherService.getAllTeachers();
        return Result.success(list);
    }

    // 模糊组合查询
    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Teacher teacher) {
        return Result.success(teacherService.selectAll(teacher));
    }

    // 按 ID 查询
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id) {
        return Result.success(teacherService.selectById(id));
    }

    // 添加
    @PostMapping("/add")
    public Result add(@RequestBody Teacher teacher) {
        teacherService.insert(teacher);
        return Result.success();
    }

    // 更新
    @PutMapping("/update")
    public Result update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        teacherService.deleteById(id);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Teacher> teachers) {
        for( Teacher teacher:teachers) {
            teacherService.insert(teacher);
        }
        return Result.success();
    }
}
