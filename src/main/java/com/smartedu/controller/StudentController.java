package com.smartedu.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Question;
import com.smartedu.entity.Student;
import com.smartedu.service.StudentService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    StudentService studentService;


    @Cacheable(value = "student:all")
    @GetMapping("/getAll")
    public Result getAllstudents() {
        List<Student> list = studentService.getALlstudents(); // 方法名应与 Service 对应
        return Result.success(list);

    }

    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Student student) {
        List<Student> list = studentService.selectAll(student);
        return Result.success(list);
    }


    @Cacheable(value = "student", key = "#id")
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id){
        Student student = studentService.selectById(id);
        return Result.success(student);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Student student,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Student> pageInfo = studentService.selectPage(student, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        studentService.addStudent(student);
        return Result.success();
    }


    @CacheEvict(value = "student:all", allEntries = true)
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        studentService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }


    @CacheEvict(value = "student:all", allEntries = true)
    @PutMapping("/update")
    public Result update(@RequestBody Student student) {
        studentService.updateStudent(student);
        return Result.success();
    }

    @GetMapping("/export")
    public Result export(HttpServletResponse response)throws Exception {
        List<Student> list = studentService.selectAll(null);
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("name", "姓名");

        writer.setOnlyAlias(true);

        writer.write(list,true);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        String fileName = "学生信息";

        response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xlsx");
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);
        writer.close();
        return  Result.success();

    }

    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("name","姓名");

        List<Student> studentlist = reader.readAll(Student.class);
        for(Student student : studentlist){
            studentService.addStudent(student);
        }
        return Result.success();
    }

    @PostMapping("/getByIds")
    public Result getStudentsByIds(@RequestBody List<Long> ids) {
        List<Student> students = new ArrayList<>();
        for (Long id : ids) {
            Student student = studentService.selectById(id);
            if (student != null) {
                students.add(student);
            }
        }
        return Result.success(students);
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Student> students) {
        for(Student student : students ) {
            studentService.addStudent(student);
        }
        return Result.success();
    }

}
