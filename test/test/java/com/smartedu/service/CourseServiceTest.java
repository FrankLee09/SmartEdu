package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Course;
import com.smartedu.mapper.CourseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CourseService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private List<Course> courseList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Java编程基础");
        testCourse.setDescription("本课程介绍Java编程的基础知识");
        testCourse.setTeacherId(101L);
        testCourse.setTeacherName("王教授");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("数据结构与算法");
        course2.setDescription("本课程介绍常见的数据结构和算法");
        course2.setTeacherId(102L);
        course2.setTeacherName("李教授");

        courseList = Arrays.asList(testCourse, course2);
    }

    @Test
    @DisplayName("测试获取所有课程")
    void testSelectAll() {
        // 创建查询条件
        Course queryCourse = new Course();

        // 模拟CourseMapper行为
        when(courseMapper.selectAll(queryCourse)).thenReturn(courseList);

        // 调用被测试方法
        List<Course> result = courseService.selectAll(queryCourse);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java编程基础", result.get(0).getTitle());
        assertEquals("数据结构与算法", result.get(1).getTitle());

        // 验证courseMapper.selectAll方法是否被调用
        verify(courseMapper, times(1)).selectAll(queryCourse);
    }

    @Test
    @DisplayName("测试根据ID查询课程")
    void testSelectById() {
        // 模拟CourseMapper行为
        when(courseMapper.selectById(1L)).thenReturn(testCourse);

        // 调用被测试方法
        Course result = courseService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java编程基础", result.getTitle());

        // 验证courseMapper.selectById方法是否被调用
        verify(courseMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询课程")
    void testSelectPage() {
        // 创建查询条件
        Course queryCourse = new Course();
        
        // 由于PageHelper是静态方法，无法直接模拟，这里只能模拟courseMapper.selectAll的返回值
        when(courseMapper.selectAll(queryCourse)).thenReturn(courseList);

        // 调用被测试方法
        PageInfo<Course> result = courseService.selectPage(queryCourse, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());

        // 验证courseMapper.selectAll方法是否被调用
        verify(courseMapper, times(1)).selectAll(queryCourse);
    }

    @Test
    @DisplayName("测试添加课程")
    void testInsert() {
        // 调用被测试方法
        courseService.insert(testCourse);

        // 验证courseMapper.insert方法是否被调用，并且参数是testCourse
        verify(courseMapper, times(1)).insert(testCourse);
    }

    @Test
    @DisplayName("测试更新课程信息")
    void testUpdate() {
        // 修改课程信息
        testCourse.setTitle("Java编程基础(已更新)");
        testCourse.setDescription("本课程介绍Java编程的基础知识，包括语法、面向对象等内容");

        // 调用被测试方法
        courseService.update(testCourse);

        // 验证courseMapper.update方法是否被调用
        verify(courseMapper, times(1)).update(testCourse);
    }

    @Test
    @DisplayName("测试删除课程")
    void testDeleteById() {
        // 调用被测试方法
        courseService.deleteById(1L);

        // 验证courseMapper.deleteById方法是否被调用
        verify(courseMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除课程")
    void testDeleteBatch() {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用被测试方法
        courseService.deleteBatch(ids);

        // 验证courseMapper.deleteById方法是否被调用两次
        verify(courseMapper, times(1)).deleteById(1L);
        verify(courseMapper, times(1)).deleteById(2L);
    }
} 