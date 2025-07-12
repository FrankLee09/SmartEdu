package com.smartedu.controller;

import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Course;
import com.smartedu.entity.ExamGrade;
import com.smartedu.entity.Student;
import com.smartedu.entity.TaskGrade;
import com.smartedu.service.ExamGradeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/examgrade")
public class ExamGradeController {

    @Resource
    private ExamGradeService examGradeService;

    @GetMapping("/getAll")
    public Result getAllGrades() {
        List<ExamGrade> list = examGradeService.getAllGrades();
        return Result.success(list);
    }

    @GetMapping("/selectAll")
    public Result selectAllGrades(ExamGrade examGrade) {
        List<ExamGrade> list = examGradeService.selectAll(examGrade);
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id) {
        TaskGrade taskGrade = examGradeService.selectById(id);
        return Result.success(taskGrade);
    }

    @PostMapping("/add")
    public Result add(@RequestBody ExamGrade examGrade) {
        examGradeService.addGrade(examGrade);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody ExamGrade examGrade) {
        examGradeService.updateGrade(examGrade);
        return Result.success();
    }

    @GetMapping("/selectPage")
    public Result selectPage(ExamGrade examGrade,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ExamGrade> pageInfo = examGradeService.selectPage(examGrade, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        examGradeService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        examGradeService.deleteBatch(ids);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<ExamGrade> examGrades) {
        for(ExamGrade examGrade : examGrades) {
            examGradeService.addGrade(examGrade);
        }
        return Result.success();
    }
}
