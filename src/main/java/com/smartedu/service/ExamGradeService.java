package com.smartedu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Exam;
import com.smartedu.entity.ExamGrade;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.ExamGradeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamGradeService {

    @Resource
    private ExamGradeMapper examGradeMapper;

    public List<ExamGrade> getAllGrades() {
        return examGradeMapper.getAllGrades();
    }

    public List<ExamGrade> selectAll(ExamGrade examGrade) {
        return examGradeMapper.selectAll(examGrade);
    }

    public TaskGrade selectById(Long id) {
        return examGradeMapper.selectById(id);
    }

    public PageInfo<ExamGrade> selectPage(ExamGrade examGrade, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ExamGrade> list = examGradeMapper.selectAll(examGrade);
        return PageInfo.of(list);
    }

    public void addGrade(ExamGrade examGrade) {
        examGradeMapper.insert(examGrade);
    }

    public void updateGrade(ExamGrade examGrade) {
        examGradeMapper.update(examGrade);
    }

    public void deleteById(Long id) {
        examGradeMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }
}
