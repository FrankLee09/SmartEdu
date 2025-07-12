package com.smartedu.mapper;

import com.smartedu.entity.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentAnswerMapper {

    List<StudentAnswer> getAllAnswers();

    List<StudentAnswer> selectAll(StudentAnswer studentAnswer);

    StudentAnswer selectById(Long id);

    void insert(StudentAnswer studentAnswer);

    void update(StudentAnswer studentAnswer);

    void deleteById(Long id);
}

