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

    List<Question> getRandomQuestions(@Param("courseId") Long courseId,
                                      @Param("kgPoint") String kgPoint,
                                      @Param("selectCount") int selectCount,
                                      @Param("shortAnswerCount") int shortAnswerCount);

    List<Question> getQuestionsByKnowledge(@Param("kgPoint") String kgPoint);

}
