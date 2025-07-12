package com.smartedu.controller;

import com.github.pagehelper.PageInfo;
import com.smartedu.common.Result;
import com.smartedu.entity.Student;
import com.smartedu.entity.Task;
import com.smartedu.entity.Teacher;
import com.smartedu.service.TaskService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskService taskService;

    /** 获取所有任务 */
    @GetMapping("/getAll")
    public Result getAll() {
        List<Task> list = taskService.getAllTasks();
        return Result.success(list);
    }

    /** 根据 ID 查询任务 */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Long id) {
        Task task = taskService.selectById(id);
        return Result.success(task);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Task task,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Task> pageInfo = taskService.selectPage(task, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/selectAll")
    public Result selectById(@RequestBody Task task) {
        List<Task> list = taskService.selectAll(task);
        return Result.success(list);
    }

//    /** 新增任务 */
//    @PostMapping("/add")
//    public Result add(@RequestBody Task task) {
//        taskService.addTask(task);
//        return Result.success();
//    }


    /** 新增任务 */
    @PostMapping("/add")
    public Result add(@RequestBody Task task) {
        taskService.addTaskJson(task);
        return Result.success();
    }

    /** 根据 ID 删除任务 */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        taskService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Long> ids) {
        taskService.deleteBatch(ids);
        return Result.success();
    }

    /** 更新任务 */
    @PutMapping("/update")
    public Result update(@RequestBody Task task) {
        taskService.updateTask(task);
        return Result.success();
    }

    // 批量添加
    @PostMapping("/addJson")
    public Result addJson(@RequestBody List<Task> tasks) {
        for( Task task : tasks) {
            taskService.addTask(task);
        }
        return Result.success();
    }
}
