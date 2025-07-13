package com.smartedu.service;

import com.smartedu.entity.Question;
import com.smartedu.mapper.QuestionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {

    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private QuestionService questionService;

    private Question testQuestion;
    private List<Question> questionList;

    @BeforeEach
    void setUp() {
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setTitle("测试题目");
        testQuestion.setDescription("这是一道测试题目");
        testQuestion.setCourseId(1L);
        testQuestion.setSelectA("选项A");
        testQuestion.setSelectB("选项B");
        testQuestion.setSelectC("选项C");
        testQuestion.setSelectD("选项D");
        testQuestion.setAnswer("A");
        testQuestion.setKgPoint("知识点1");
        testQuestion.setTag("选择题");
        testQuestion.setScore(10.0);
        
        Question question2 = new Question();
        question2.setId(2L);
        question2.setTitle("测试题目2");
        question2.setDescription("这是第二道测试题目");
        question2.setCourseId(1L);
        question2.setAnswer("这是答案");
        question2.setKgPoint("知识点2");
        question2.setTag("简答题");
        question2.setScore(20.0);
        
        questionList = new ArrayList<>();
        questionList.add(testQuestion);
        questionList.add(question2);
    }

    @Test
    @DisplayName("测试获取所有题目")
    void testGetAllQuestions() {
        when(questionMapper.getAllQuestions()).thenReturn(questionList);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("测试题目", result.get(0).getTitle());
        assertEquals("测试题目2", result.get(1).getTitle());
        
        verify(questionMapper, times(1)).getAllQuestions();
    }

    @Test
    @DisplayName("测试根据条件查询题目")
    void testSelectAll() {
        Question query = new Question();
        query.setCourseId(1L);
        
        when(questionMapper.selectAll(query)).thenReturn(questionList);

        List<Question> result = questionService.selectAll(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(questionMapper, times(1)).selectAll(query);
    }

    @Test
    @DisplayName("测试根据ID查询题目")
    void testSelectById() {
        when(questionMapper.selectById(1L)).thenReturn(testQuestion);

        Question result = questionService.selectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试题目", result.getTitle());
        assertEquals("知识点1", result.getKgPoint());
        
        verify(questionMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加题目")
    void testInsert() {
        doNothing().when(questionMapper).insert(testQuestion);

        questionService.insert(testQuestion);
        
        verify(questionMapper, times(1)).insert(testQuestion);
    }

    @Test
    @DisplayName("测试更新题目")
    void testUpdate() {
        doNothing().when(questionMapper).update(testQuestion);

        testQuestion.setTitle("更新后的题目标题");
        testQuestion.setScore(15.0);
        questionService.update(testQuestion);
        
        verify(questionMapper, times(1)).update(testQuestion);
    }

    @Test
    @DisplayName("测试删除题目")
    void testDeleteById() {
        doNothing().when(questionMapper).deleteById(1L);

        questionService.deleteById(1L);
        
        verify(questionMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试获取随机题目")
    void testGetRandomQuestions() {
        Long courseId = 1L;
        String kgPoint = "知识点1";
        int selectCount = 3;
        int shortAnswerCount = 2;
        
        List<Question> randomQuestions = new ArrayList<>();
        randomQuestions.add(testQuestion);
        
        when(questionMapper.getRandomQuestions(courseId, kgPoint, selectCount, shortAnswerCount))
            .thenReturn(randomQuestions);

        List<Question> result = questionService.getRandomQuestions(courseId, kgPoint, selectCount, shortAnswerCount);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试题目", result.get(0).getTitle());
        
        verify(questionMapper, times(1)).getRandomQuestions(courseId, kgPoint, selectCount, shortAnswerCount);
    }
} 