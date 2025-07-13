package com.smartedu.service;

import com.smartedu.entity.Exam;
import com.smartedu.mapper.ExamMapper;
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
 * ExamService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class ExamServiceTest {

    @Mock
    private ExamMapper examMapper;

    @InjectMocks
    private ExamService examService;

    private Exam testExam;
    private List<Exam> examList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testExam = new Exam();
        testExam.setId(1L);
        testExam.setTitle("Java期中考试");
        testExam.setCourseId(101L);
        testExam.setStartTime("2023-10-15 09:00:00");
        testExam.setEndTime("2023-10-15 11:00:00");
        testExam.setTotalScore(100.0);
        testExam.setQuestionIds(Arrays.asList(1L, 2L, 3L));
        testExam.setTag("期中考试");

        Exam exam2 = new Exam();
        exam2.setId(2L);
        exam2.setTitle("数据库期末考试");
        exam2.setCourseId(102L);
        exam2.setStartTime("2023-12-20 14:00:00");
        exam2.setEndTime("2023-12-20 16:00:00");
        exam2.setTotalScore(100.0);
        exam2.setQuestionIds(Arrays.asList(4L, 5L, 6L));
        exam2.setTag("期末考试");

        examList = Arrays.asList(testExam, exam2);
    }

    @Test
    @DisplayName("测试获取所有考试")
    void testGetAllExams() {
        // 模拟ExamMapper行为
        when(examMapper.getAllExams()).thenReturn(examList);

        // 调用被测试方法
        List<Exam> result = examService.getAllExams();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java期中考试", result.get(0).getTitle());
        assertEquals("数据库期末考试", result.get(1).getTitle());

        // 验证examMapper.getAllExams方法是否被调用
        verify(examMapper, times(1)).getAllExams();
    }

    @Test
    @DisplayName("测试根据条件查询考试")
    void testSelectAll() {
        // 创建查询条件
        Exam queryExam = new Exam();
        queryExam.setTag("期中考试");

        // 模拟ExamMapper行为
        when(examMapper.selectAll(queryExam)).thenReturn(Collections.singletonList(testExam));

        // 调用被测试方法
        List<Exam> result = examService.selectAll(queryExam);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java期中考试", result.get(0).getTitle());
        assertEquals("期中考试", result.get(0).getTag());

        // 验证examMapper.selectAll方法是否被调用
        verify(examMapper, times(1)).selectAll(queryExam);
    }

    @Test
    @DisplayName("测试根据ID查询考试")
    void testSelectById() {
        // 模拟ExamMapper行为
        when(examMapper.selectById(1L)).thenReturn(testExam);

        // 调用被测试方法
        Exam result = examService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java期中考试", result.getTitle());
        assertEquals(101L, result.getCourseId());

        // 验证examMapper.selectById方法是否被调用
        verify(examMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加考试")
    void testInsert() {
        // 调用被测试方法
        examService.insert(testExam);

        // 验证examMapper.insert方法是否被调用，并且参数是testExam
        verify(examMapper, times(1)).insert(testExam);
    }

    @Test
    @DisplayName("测试更新考试信息")
    void testUpdate() {
        // 修改考试信息
        testExam.setTitle("Java期中考试(已更新)");
        testExam.setEndTime("2023-10-15 12:00:00");

        // 调用被测试方法
        examService.update(testExam);

        // 验证examMapper.update方法是否被调用
        verify(examMapper, times(1)).update(testExam);
    }

    @Test
    @DisplayName("测试删除考试")
    void testDeleteById() {
        // 调用被测试方法
        examService.deleteById(1L);

        // 验证examMapper.deleteById方法是否被调用
        verify(examMapper, times(1)).deleteById(1L);
    }
} 