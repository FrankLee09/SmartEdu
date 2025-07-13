package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.TaskGradeMapper;
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
 * TaskGradeService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class TaskGradeServiceTest {

    @Mock
    private TaskGradeMapper taskGradeMapper;

    @InjectMocks
    private TaskGradeService taskGradeService;

    private TaskGrade testTaskGrade;
    private List<TaskGrade> taskGradeList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testTaskGrade = new TaskGrade();
        testTaskGrade.setId(1L);
        testTaskGrade.setStudentId(101L);
        testTaskGrade.setTaskId(201L);
        testTaskGrade.setScore(85.5);
        testTaskGrade.setGradedate("2023-12-15");

        TaskGrade taskGrade2 = new TaskGrade();
        taskGrade2.setId(2L);
        taskGrade2.setStudentId(102L);
        taskGrade2.setTaskId(202L);
        taskGrade2.setScore(92.0);
        taskGrade2.setGradedate("2023-12-16");

        taskGradeList = Arrays.asList(testTaskGrade, taskGrade2);
    }

    @Test
    @DisplayName("测试获取所有成绩")
    void testGetAllGrades() {
        // 模拟TaskGradeMapper行为
        when(taskGradeMapper.getAllGrades()).thenReturn(taskGradeList);

        // 调用被测试方法
        List<TaskGrade> result = taskGradeService.getAllGrades();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(85.5, result.get(0).getScore());
        assertEquals(92.0, result.get(1).getScore());

        // 验证taskGradeMapper.getAllGrades方法是否被调用
        verify(taskGradeMapper, times(1)).getAllGrades();
    }

    @Test
    @DisplayName("测试根据条件查询成绩")
    void testSelectAll() {
        // 创建查询条件
        TaskGrade queryTaskGrade = new TaskGrade();
        queryTaskGrade.setStudentId(101L);

        // 模拟TaskGradeMapper行为
        when(taskGradeMapper.selectAll(queryTaskGrade)).thenReturn(Collections.singletonList(testTaskGrade));

        // 调用被测试方法
        List<TaskGrade> result = taskGradeService.selectAll(queryTaskGrade);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(85.5, result.get(0).getScore());
        assertEquals(101L, result.get(0).getStudentId());

        // 验证taskGradeMapper.selectAll方法是否被调用
        verify(taskGradeMapper, times(1)).selectAll(queryTaskGrade);
    }

    @Test
    @DisplayName("测试根据ID查询成绩")
    void testSelectById() {
        // 模拟TaskGradeMapper行为
        when(taskGradeMapper.selectById(1L)).thenReturn(testTaskGrade);

        // 调用被测试方法
        TaskGrade result = taskGradeService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(85.5, result.getScore());
        assertEquals(101L, result.getStudentId());

        // 验证taskGradeMapper.selectById方法是否被调用
        verify(taskGradeMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加成绩")
    void testAddGrade() {
        // 调用被测试方法
        taskGradeService.addGrade(testTaskGrade);

        // 验证taskGradeMapper.insert方法是否被调用，并且参数是testTaskGrade
        verify(taskGradeMapper, times(1)).insert(testTaskGrade);
    }

    @Test
    @DisplayName("测试更新成绩信息")
    void testUpdateGrade() {
        // 修改成绩信息
        testTaskGrade.setScore(90.0);

        // 调用被测试方法
        taskGradeService.updateGrade(testTaskGrade);

        // 验证taskGradeMapper.update方法是否被调用
        verify(taskGradeMapper, times(1)).update(testTaskGrade);
    }

    @Test
    @DisplayName("测试分页查询成绩")
    void testSelectPage() {
        // 创建查询条件
        TaskGrade queryTaskGrade = new TaskGrade();
        
        // 由于PageHelper是静态方法，无法直接模拟，这里只能模拟taskGradeMapper.selectAll的返回值
        when(taskGradeMapper.selectAll(queryTaskGrade)).thenReturn(taskGradeList);

        // 调用被测试方法
        PageInfo<TaskGrade> result = taskGradeService.selectPage(queryTaskGrade, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(85.5, result.getList().get(0).getScore());
        assertEquals(92.0, result.getList().get(1).getScore());

        // 验证taskGradeMapper.selectAll方法是否被调用
        verify(taskGradeMapper, times(1)).selectAll(queryTaskGrade);
    }

    @Test
    @DisplayName("测试删除成绩")
    void testDeleteById() {
        // 调用被测试方法
        taskGradeService.deleteById(1L);

        // 验证taskGradeMapper.deleteById方法是否被调用
        verify(taskGradeMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除成绩")
    void testDeleteBatch() {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用被测试方法
        taskGradeService.deleteBatch(ids);

        // 验证taskGradeMapper.deleteById方法是否被调用两次
        verify(taskGradeMapper, times(1)).deleteById(1L);
        verify(taskGradeMapper, times(1)).deleteById(2L);
    }
} 