package com.smartedu.mapper;

import com.smartedu.entity.StudentAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentAnswerMapper {

    List<StudentAnswer> getAllAnswers();

    List<StudentAnswer> selectAll(StudentAnswer studentAnswer);

    StudentAnswer selectById(Long id);

    void insert(StudentAnswer studentAnswer);

    void update(StudentAnswer studentAnswer);

    StudentAnswer getByExamAndQuestion(@Param("examId") Long examId,
                                       @Param("questionId") Long questionId,
                                       @Param("studentId") Long studentId);

    void deleteById(Long id);
}

