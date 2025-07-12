package com.smartedu.mapper;

import com.smartedu.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamMapper {
    List<Exam> getAllExams();

    List<Exam> selectAll(Exam exam);

    Exam selectById(Long id);

    void insert(Exam exam);

    void update(Exam exam);

    void deleteById(Long id);
}
