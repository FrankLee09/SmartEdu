package com.smartedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Course;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.TaskGradeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskGradeService {

    @Resource
    private TaskGradeMapper taskGradeMapper;

    public List<TaskGrade> getAllGrades() {
        return taskGradeMapper.getAllGrades();
    }

    public List<TaskGrade> selectAll(TaskGrade taskGrade) {
        return taskGradeMapper.selectAll(taskGrade);
    }

    public TaskGrade selectById(Long id) {
        return taskGradeMapper.selectById(id);
    }

    public void addGrade(TaskGrade taskGrade) {
        taskGradeMapper.insert(taskGrade);
    }

    public void updateGrade(TaskGrade taskGrade) {
        taskGradeMapper.update(taskGrade);
    }

    public PageInfo<TaskGrade> selectPage(TaskGrade taskGrade, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TaskGrade> list = taskGradeMapper.selectAll(taskGrade);
        return PageInfo.of(list);
    }


    public void deleteById(Long id) {
        taskGradeMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }

    public List<TaskGrade> getGradesByStudentId(Long studentId) {
        return taskGradeMapper.selectByStudentId(studentId);
    }

    public List<TaskGrade> selectTop7ByIdDesc(Long studentId) {

        return taskGradeMapper.selectTop7ByIdDesc(studentId);
    }

}
