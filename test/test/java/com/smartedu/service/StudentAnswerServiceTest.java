package com.smartedu.service;

import com.smartedu.entity.StudentAnswer;
import com.smartedu.mapper.StudentAnswerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StudentAnswerService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class StudentAnswerServiceTest {

    @Mock
    private StudentAnswerMapper studentAnswerMapper;

    @InjectMocks
    private StudentAnswerService studentAnswerService;

    private StudentAnswer testStudentAnswer;
    private List<StudentAnswer> studentAnswerList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testStudentAnswer = new StudentAnswer();
        testStudentAnswer.setId(1L);
        testStudentAnswer.setStudentId(101L);
        testStudentAnswer.setQuestionId(201L);
        testStudentAnswer.setExamId(301L);
        testStudentAnswer.setGetscore(8.5);
        testStudentAnswer.setAnswer("这是第一个问题的答案");

        StudentAnswer studentAnswer2 = new StudentAnswer();
        studentAnswer2.setId(2L);
        studentAnswer2.setStudentId(102L);
        studentAnswer2.setQuestionId(202L);
        studentAnswer2.setExamId(301L);
        studentAnswer2.setGetscore(9.0);
        studentAnswer2.setAnswer("这是第二个问题的答案");

        studentAnswerList = Arrays.asList(testStudentAnswer, studentAnswer2);
    }

    @Test
    @DisplayName("测试获取所有学生答案")
    void testGetAllAnswers() {
        // 模拟StudentAnswerMapper行为
        when(studentAnswerMapper.getAllAnswers()).thenReturn(studentAnswerList);

        // 调用被测试方法
        List<StudentAnswer> result = studentAnswerService.getAllAnswers();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getStudentId());
        assertEquals(102L, result.get(1).getStudentId());

        // 验证studentAnswerMapper.getAllAnswers方法是否被调用
        verify(studentAnswerMapper, times(1)).getAllAnswers();
    }

    @Test
    @DisplayName("测试根据条件查询学生答案")
    void testSelectAll() {
        // 创建查询条件
        StudentAnswer queryStudentAnswer = new StudentAnswer();
        queryStudentAnswer.setStudentId(101L);

        // 模拟StudentAnswerMapper行为
        when(studentAnswerMapper.selectAll(queryStudentAnswer)).thenReturn(Collections.singletonList(testStudentAnswer));

        // 调用被测试方法
        List<StudentAnswer> result = studentAnswerService.selectAll(queryStudentAnswer);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getStudentId());
        assertEquals(8.5, result.get(0).getGetscore());

        // 验证studentAnswerMapper.selectAll方法是否被调用
        verify(studentAnswerMapper, times(1)).selectAll(queryStudentAnswer);
    }

    @Test
    @DisplayName("测试根据ID查询学生答案")
    void testSelectById() {
        // 模拟StudentAnswerMapper行为
        when(studentAnswerMapper.selectById(1L)).thenReturn(testStudentAnswer);

        // 调用被测试方法
        StudentAnswer result = studentAnswerService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(101L, result.getStudentId());
        assertEquals("这是第一个问题的答案", result.getAnswer());

        // 验证studentAnswerMapper.selectById方法是否被调用
        verify(studentAnswerMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加学生答案")
    void testAdd() {
        // 调用被测试方法
        studentAnswerService.add(testStudentAnswer);

        // 验证studentAnswerMapper.insert方法是否被调用，并且参数是testStudentAnswer
        verify(studentAnswerMapper, times(1)).insert(testStudentAnswer);
    }

    @Test
    @DisplayName("测试更新学生答案信息")
    void testUpdate() {
        // 修改学生答案信息
        testStudentAnswer.setGetscore(9.5);
        testStudentAnswer.setAnswer("这是更新后的答案");

        // 调用被测试方法
        studentAnswerService.update(testStudentAnswer);

        // 验证studentAnswerMapper.update方法是否被调用
        verify(studentAnswerMapper, times(1)).update(testStudentAnswer);
    }

    @Test
    @DisplayName("测试删除学生答案")
    void testDeleteById() {
        // 调用被测试方法
        studentAnswerService.deleteById(1L);

        // 验证studentAnswerMapper.deleteById方法是否被调用
        verify(studentAnswerMapper, times(1)).deleteById(1L);
    }
} 