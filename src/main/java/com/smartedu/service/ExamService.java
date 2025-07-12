package com.smartedu.service;

import com.smartedu.entity.Exam;
import com.smartedu.entity.Question;
import com.smartedu.mapper.ExamMapper;
import com.smartedu.mapper.QuestionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExamService {

    @Resource
    private ExamMapper examMapper;

    @Resource
    private QuestionMapper questionMapper;

    public List<Exam> getAllExams() {
        return examMapper.getAllExams();
    }

    public List<Exam> selectAll(Exam exam) {
        return examMapper.selectAll(exam);
    }

    public Exam selectById(Long id) {
        return examMapper.selectById(id);
    }

    public List<Question> selctQsByexamId(Long id){
        Exam exam = examMapper.selectById(id);
        List <Long> questionIds = exam.getQuestionIds();
        System.out.println(questionIds);
        List<Question> questionList = new ArrayList<>();

        for (Long questionId : questionIds) {
            Question question = questionMapper.selectById(questionId);
            if (question != null) {
                questionList.add(question);
            }
        }

        return questionList;



    }

    public void insert(Exam exam) {
        examMapper.insert(exam);
    }

    public void update(Exam exam) {
        examMapper.update(exam);
    }

    public void deleteById(Long id) {
        examMapper.deleteById(id);
    }
}
