package com.smartedu.controller;

import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Course;
import com.smartedu.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    CourseService courseService;

    @GetMapping("/getAll")
    public Result getAll() {
        List<Course> list  = courseService.getAllCourses();
        return Result.success(list);
    }

    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Course course) {
        List<Course> list = courseService.selectAll(course);
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(courseService.selectById(id));
    }

    @GetMapping("/selectPage")
    public Result selectPage(Course course,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Course> pageInfo = courseService.selectPage(course, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        courseService.insert(course);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Course course) {
        courseService.update(course);
        return Result.success();
    }
    
    @DeleteMapping("/delete/{id}")
    public Result  delete(@PathVariable Long id) {
        courseService.deleteById(id);
        return Result.success();
    }
    

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        courseService.deleteBatch(ids);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Course> courses) {
        for( Course course : courses) {
            courseService.insert(course);
        }
        return Result.success();
    }
}
