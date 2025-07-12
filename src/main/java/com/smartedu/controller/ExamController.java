package com.smartedu.controller;

import com.smartedu.common.Result;
import com.smartedu.entity.*;
import com.smartedu.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private ExamService examService;

    // 查询所有
    @GetMapping("/getAll")
    public Result getAll() {
        return Result.success(examService.getAllExams());
    }

    // 模糊组合查询
    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Exam exam) {
        return Result.success(examService.selectAll(exam));
    }

    // 按 ID 查询
    @GetMapping("/selectById/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(examService.selectById(id));
    }

    //通过ID查询当前考试中的所有题目
    @GetMapping("/selectQuestionsById/{id}")
    public Result selectQuestionsById(@PathVariable Long id){
        List<Question> questionList = examService.selctQsByexamId(id);
        return Result.success(questionList);


    }
    // 添加
    @PostMapping("/add")
    public Result add(@RequestBody Exam exam) {
        examService.insert(exam);
        return Result.success();
    }

    // 更新
    @PutMapping("/update")
    public Result update(@RequestBody Exam exam) {
        examService.update(exam);
        return Result.success();
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        examService.deleteById(id);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Exam> exams) {
        for( Exam exam:exams) {
            examService.insert(exam);
        }
        return Result.success();
    }




}
