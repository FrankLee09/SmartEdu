package com.smartedu.mapper;

import com.smartedu.entity.ExamGrade;
import com.smartedu.entity.TaskGrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamGradeMapper {

    List<ExamGrade> getAllGrades();

    List<ExamGrade> selectAll(ExamGrade examGrade);

    TaskGrade selectById(Long id);

    int insert(ExamGrade examGrade);

    int update(ExamGrade examGrade);

    int deleteById(Long id);
}
