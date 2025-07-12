package com.smartedu.mapper;

import com.smartedu.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {
    List<Question> getAllQuestions();

    List<Question> selectAll(Question question);

    Question selectById(Long id);

    void insert(Question question);

    void update(Question question);

    void deleteById(Long id);

    List<Question> getRandomQuestions(Long courseId, String KGpoint, int selectCount, int shortAnswerCount);

}
