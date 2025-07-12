package com.smartedu.service;

import com.smartedu.entity.Question;
import com.smartedu.mapper.QuestionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    public List<Question> getAllQuestions() {
        return questionMapper.getAllQuestions();
    }

    public List<Question> selectAll(Question question) {
        return questionMapper.selectAll(question);
    }

    public Question selectById(Long id) {
        return questionMapper.selectById(id);
    }

    public void insert(Question question) {
        questionMapper.insert(question);
    }

    public void update(Question question) {
        questionMapper.update(question);
    }

    public void deleteById(Long id) {
        questionMapper.deleteById(id);
    }

    public List<Question> getRandomQuestions(Long courseId,String KGpoint, int selectCount, int shortAnswerCount) {
        return questionMapper.getRandomQuestions(courseId, KGpoint,selectCount, shortAnswerCount);
    }
}
