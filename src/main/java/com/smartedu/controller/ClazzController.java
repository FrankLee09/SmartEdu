package com.smartedu.controller;

import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Student;
import com.smartedu.entity.Submission;
import com.smartedu.service.ClazzService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.smartedu.entity.Clazz; // 假设你已将 Class 改为 Clazz

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClazzController {

    @Resource
    private ClazzService classService;

    @GetMapping("/getAll")
    public Result getAll() {
        List<Clazz> list = classService.getAllclasses(); // 方法名应与 Service 对应
        return Result.success(list);
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id){
        Clazz clazz = classService.selectById(id);
        return Result.success(clazz);

    }


    @GetMapping("/selectPage")
    public Result selectPage(Clazz clazz,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Clazz> pageInfo = classService.selectPage(clazz, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/selectAll")
    public Result selectAll(@RequestBody Clazz clazz) {
        List<Clazz> list =  classService.selectAll(clazz);
        // 方法名应与 Service 对应
        return Result.success(list);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Clazz clazz) {
        classService.addClazz(clazz);
        return Result.success();
    }
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
       classService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        classService.deleteBatch(ids);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Clazz clazz) {
        classService.updateClazz(clazz);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Clazz> clazzes) {
        for(Clazz clazz : clazzes) {
            classService.addClazz(clazz);
        }
        return Result.success();
    }


}
