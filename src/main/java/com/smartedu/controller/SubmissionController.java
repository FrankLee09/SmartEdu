package com.smartedu.controller;

import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Student;
import com.smartedu.entity.Submission;
import com.smartedu.service.SubmissionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
public class SubmissionController {
    @Resource
    SubmissionService submissionService;

    @GetMapping("/getAll")
    public Result selectAll() {
        List<Submission> list = submissionService.getAllSubmissions(); // 方法名应与 Service 对应
        return Result.success(list);
    }

    @PostMapping("/selectAll")
    public Result selectAll(@RequestBody Submission submission) {
        List<Submission> list = submissionService.selectAll(submission);
        return Result.success(list);
    }


    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id){
        Submission submission = submissionService.selectById(id);
        return Result.success(submission);

    }

    @GetMapping("/selectPage")
    public Result selectPage(Submission submission,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Submission> pageInfo = submissionService.selectPage(submission, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Submission submission) {
        submissionService.addSubmission(submission);
        return Result.success();
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        submissionService.deleteById(id);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Submission submission) {
        submissionService.updateSubmission(submission);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Submission> submissions) {
        for(Submission submission:submissions) {
            submissionService.addSubmission(submission);
        }
        return Result.success();
    }
}
