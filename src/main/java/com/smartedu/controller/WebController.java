package com.smartedu.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.smartedu.common.Result;
import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.entity.TaskGrade;
import com.smartedu.entity.Teacher;
import com.smartedu.exception.CustomException;
import com.smartedu.service.StudentService;
import com.smartedu.service.TaskGradeService;
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

    @Resource
    private TaskGradeService taskGradeService;


 
    

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


//    @GetMapping("/barData")
//    public Result getBarData() {
//        Map<String, Object> map = new HashMap<>();
//        List<TaskGrade> taskGradeList = taskGradeService.selectAll(null);
//
//        Set<Double> taskgradeSet = taskGradeList.stream().map(TaskGrade::getScore).collect(Collectors.toSet());
//        map.put("score", taskgradeSet);
//        List<Long> countList = new ArrayList<>();
//        for (Double score:taskgradeSet ) {
//            long count = taskGradeList.stream().filter(article -> article.getScore().equals(score)).count();
//            countList.add(count);
//        }
//        map.put("count", countList);
//        return Result.success(map);
//    }


    @GetMapping("/barData")
    public Result getBarData(@RequestParam("studentId") Long studentId) {
        Map<String, Object> map = new HashMap<>();
        // 根据学生ID查询该学生的所有任务成绩
        List<TaskGrade> taskGradeList = taskGradeService.getGradesByStudentId(studentId);

        // 初始化分数区间计数器
        int[] scoreRanges = new int[5]; // 0-19, 20-39, 40-59, 60-79, 80-100

        for (TaskGrade taskGrade : taskGradeList) {
            Double scoreObj = taskGrade.getScore();
            if (scoreObj == null) {
                continue; // 跳过 score 为 null 的记录
            }
            double score = scoreObj;

            if (score < 20) {
                scoreRanges[0]++;
            } else if (score < 40) {
                scoreRanges[1]++;
            } else if (score < 60) {
                scoreRanges[2]++;
            } else if (score < 80) {
                scoreRanges[3]++;
            } else {
                scoreRanges[4]++;
            }
        }

        // 分数区间标签
        List<String> tag = Arrays.asList("0-19", "20-39", "40-59", "60-79", "80-100");
        // 每个区间的数量
        List<Long> countList = Arrays.stream(scoreRanges).mapToLong(i -> i).boxed().collect(Collectors.toList());

        map.put("tag", tag);
        map.put("count", countList);
        return Result.success(map);
    }

//    @GetMapping("/lineData")
//    public Result getLineData(@RequestParam("studentId") Long studentId) {
//        Map<String, Object> map = new HashMap<>();
//        // 根据学生ID查询该学生最近七次的任务成绩，并按时间排序
//        List<TaskGrade> taskGradeList = taskGradeService.selectTop7ByIdDesc(studentId);
//
//        // 提取成绩和日期
//        List<Double> scores = taskGradeList.stream().map(TaskGrade::getScore).collect(Collectors.toList());
//        List<String> dates = taskGradeList.stream().map(taskGrade -> taskGrade.getGradedate().toString()).collect(Collectors.toList());
//
//        map.put("date", dates);
//        map.put("count", scores);
//        return Result.success(map);
//    }


    @GetMapping("/lineData")
    public Result getLineData(@RequestParam("studentId") Long studentId) {
        Map<String, Object> map = new HashMap<>();
        // 根据学生ID查询该学生最近七次的任务成绩，并按时间排序
        List<TaskGrade> taskGradeList = taskGradeService.selectTop7ByIdDesc(studentId);

        // 提取成绩和日期（处理 gradedate 可能为 null 的情况）
        List<Double> scores = taskGradeList.stream()
                .map(TaskGrade::getScore)
                .collect(Collectors.toList());
        List<String> dates = taskGradeList.stream()
                .map(taskGrade -> taskGrade.getGradedate() != null ? taskGrade.getGradedate().toString() : "无日期")
                .collect(Collectors.toList());

        map.put("date", dates);
        map.put("count", scores);
        return Result.success(map);
    }




}
