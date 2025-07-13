package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.ExamGrade;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.ExamGradeMapper;
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
 * ExamGradeService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class ExamGradeServiceTest {

    @Mock
    private ExamGradeMapper examGradeMapper;

    @InjectMocks
    private ExamGradeService examGradeService;

    private ExamGrade testExamGrade;
    private TaskGrade testTaskGrade;
    private List<ExamGrade> examGradeList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testExamGrade = new ExamGrade();
        testExamGrade.setId(1L);
        testExamGrade.setStudentId(101L);
        testExamGrade.setExamId(201L);
        testExamGrade.setScore(85.5);
        testExamGrade.setGradedate("2023-12-15");

        ExamGrade examGrade2 = new ExamGrade();
        examGrade2.setId(2L);
        examGrade2.setStudentId(102L);
        examGrade2.setExamId(202L);
        examGrade2.setScore(92.0);
        examGrade2.setGradedate("2023-12-16");

        examGradeList = Arrays.asList(testExamGrade, examGrade2);
        
        // 准备TaskGrade测试数据（用于selectById方法）
        testTaskGrade = new TaskGrade();
        testTaskGrade.setId(1L);
        testTaskGrade.setStudentId(101L);
        testTaskGrade.setTaskId(201L);
        testTaskGrade.setScore(85.5);
        testTaskGrade.setGradedate("2023-12-15");
    }

    @Test
    @DisplayName("测试获取所有考试成绩")
    void testGetAllGrades() {
        // 模拟ExamGradeMapper行为
        when(examGradeMapper.getAllGrades()).thenReturn(examGradeList);

        // 调用被测试方法
        List<ExamGrade> result = examGradeService.getAllGrades();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(85.5, result.get(0).getScore());
        assertEquals(92.0, result.get(1).getScore());

        // 验证examGradeMapper.getAllGrades方法是否被调用
        verify(examGradeMapper, times(1)).getAllGrades();
    }

    @Test
    @DisplayName("测试根据条件查询考试成绩")
    void testSelectAll() {
        // 创建查询条件
        ExamGrade queryExamGrade = new ExamGrade();
        queryExamGrade.setStudentId(101L);

        // 模拟ExamGradeMapper行为
        when(examGradeMapper.selectAll(queryExamGrade)).thenReturn(Collections.singletonList(testExamGrade));

        // 调用被测试方法
        List<ExamGrade> result = examGradeService.selectAll(queryExamGrade);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(85.5, result.get(0).getScore());
        assertEquals(101L, result.get(0).getStudentId());

        // 验证examGradeMapper.selectAll方法是否被调用
        verify(examGradeMapper, times(1)).selectAll(queryExamGrade);
    }

    @Test
    @DisplayName("测试根据ID查询成绩")
    void testSelectById() {
        // 模拟ExamGradeMapper行为
        when(examGradeMapper.selectById(1L)).thenReturn(testTaskGrade);

        // 调用被测试方法
        TaskGrade result = examGradeService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(85.5, result.getScore());
        assertEquals(101L, result.getStudentId());

        // 验证examGradeMapper.selectById方法是否被调用
        verify(examGradeMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询考试成绩")
    void testSelectPage() {
        // 创建查询条件
        ExamGrade queryExamGrade = new ExamGrade();
        
        // 由于PageHelper是静态方法，无法直接模拟，这里只能模拟examGradeMapper.selectAll的返回值
        when(examGradeMapper.selectAll(queryExamGrade)).thenReturn(examGradeList);

        // 调用被测试方法
        PageInfo<ExamGrade> result = examGradeService.selectPage(queryExamGrade, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(85.5, result.getList().get(0).getScore());
        assertEquals(92.0, result.getList().get(1).getScore());

        // 验证examGradeMapper.selectAll方法是否被调用
        verify(examGradeMapper, times(1)).selectAll(queryExamGrade);
    }

    @Test
    @DisplayName("测试添加考试成绩")
    void testAddGrade() {
        // 调用被测试方法
        examGradeService.addGrade(testExamGrade);

        // 验证examGradeMapper.insert方法是否被调用，并且参数是testExamGrade
        verify(examGradeMapper, times(1)).insert(testExamGrade);
    }

    @Test
    @DisplayName("测试更新考试成绩信息")
    void testUpdateGrade() {
        // 修改成绩信息
        testExamGrade.setScore(90.0);

        // 调用被测试方法
        examGradeService.updateGrade(testExamGrade);

        // 验证examGradeMapper.update方法是否被调用
        verify(examGradeMapper, times(1)).update(testExamGrade);
    }

    @Test
    @DisplayName("测试删除考试成绩")
    void testDeleteById() {
        // 调用被测试方法
        examGradeService.deleteById(1L);

        // 验证examGradeMapper.deleteById方法是否被调用
        verify(examGradeMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除考试成绩")
    void testDeleteBatch() {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用被测试方法
        examGradeService.deleteBatch(ids);

        // 验证examGradeMapper.deleteById方法是否被调用两次
        verify(examGradeMapper, times(1)).deleteById(1L);
        verify(examGradeMapper, times(1)).deleteById(2L);
    }
} 