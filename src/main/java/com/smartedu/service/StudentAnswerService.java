package com.smartedu.service;

import com.smartedu.entity.StudentAnswer;
import com.smartedu.mapper.StudentAnswerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAnswerService {

    @Resource
    private StudentAnswerMapper studentAnswerMapper;

    public List<StudentAnswer> getAllAnswers() {
        return studentAnswerMapper.getAllAnswers();
    }

    public List<StudentAnswer> selectAll(StudentAnswer studentAnswer) {
        return studentAnswerMapper.selectAll(studentAnswer);
    }

    public StudentAnswer getByExamAndQuestion(Long examId, Long questionId, Long studentId) {
        return studentAnswerMapper.getByExamAndQuestion(examId, questionId, studentId);
    }

    public StudentAnswer selectById(Long id) {
        return studentAnswerMapper.selectById(id);
    }

    public void add(StudentAnswer studentAnswer) {
        studentAnswerMapper.insert(studentAnswer);
    }

    public void update(StudentAnswer studentAnswer) {
        studentAnswerMapper.update(studentAnswer);
    }

    public void deleteById(Long id) {
        studentAnswerMapper.deleteById(id);
    }
}
