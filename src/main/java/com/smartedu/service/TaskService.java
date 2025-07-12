package com.smartedu.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Student;
import com.smartedu.entity.Task;
import com.smartedu.mapper.TaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Resource
    TaskMapper taskMapper;

    /** 获取所有任务 */
    public List<Task> getAllTasks() {
        return taskMapper.getAllTasks();
    }

    /** 根据 ID 查询任务 */
    public Task selectById(Long id) {
        return taskMapper.selectById(id);
    }

    public List<Task> selectAll(Task task) {
        return taskMapper.selectAll(task);
    }
    /** 新增任务 */
    public void addTask(Task task) {
        taskMapper.insert(task);
    }
    /** 更新任务 */
    public void updateTask(Task task) {
        taskMapper.update(task);
    }

    public PageInfo<Task> selectPage(Task task, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Task> list = taskMapper.selectAll(task);
        return PageInfo.of(list);
    }

    /** 新增任务 */
    public void addTaskJson(Task task) {
        taskMapper.insertJson(task);
    }
    /** 根据 ID 删除任务 */
    public void deleteById(Long id) {
        taskMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }
}
