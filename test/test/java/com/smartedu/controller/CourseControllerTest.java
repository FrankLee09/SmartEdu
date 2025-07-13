package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Course;
import com.smartedu.mapper.ClazzMapper;
import com.smartedu.mapper.CourseMapper;
import com.smartedu.mapper.StudentMapper;
import com.smartedu.mapper.TeacherMapper;
import com.smartedu.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@Import(TestConfig.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;
    
    @MockBean
    private CourseMapper courseMapper;
    
    @MockBean
    private StudentMapper studentMapper;
    
    @MockBean
    private TeacherMapper teacherMapper;
    
    @MockBean
    private ClazzMapper clazzMapper;

    private Course course1;
    private Course course2;
    private List<Course> courseList;
    private PageInfo<Course> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java编程基础");
        course1.setDescription("本课程介绍Java编程的基础知识");
        course1.setTeacherId(101L);
        course1.setTeacherName("王教授");
        course1.setTime("2023-07-01 10:00:00");

        course2 = new Course();
        course2.setId(2L);
        course2.setTitle("数据结构与算法");
        course2.setDescription("本课程介绍常见的数据结构和算法");
        course2.setTeacherId(102L);
        course2.setTeacherName("李教授");
        course2.setTime("2023-07-02 14:00:00");

        courseList = Arrays.asList(course1, course2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(courseList);
    }

    @Test
    @DisplayName("测试获取所有课程")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(courseService.getAllCourses()).thenReturn(courseList);

        // 执行请求并验证结果
        mockMvc.perform(get("/course/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java编程基础"))
                .andExpect(jsonPath("$.data[1].title").value("数据结构与算法"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("测试根据条件查询课程")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Course queryCourse = new Course();
        queryCourse.setTeacherId(101L);

        // 模拟Service行为
        when(courseService.selectAll(any(Course.class))).thenReturn(Arrays.asList(course1));

        // 执行请求并验证结果
        mockMvc.perform(post("/course/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java编程基础"))
                .andExpect(jsonPath("$.data[0].teacherId").value(101));

        // 验证service方法是否被调用
        verify(courseService, times(1)).selectAll(any(Course.class));
    }

    @Test
    @DisplayName("测试根据ID查询课程")
    void testGetById() throws Exception {
        // 模拟Service行为
        when(courseService.selectById(1L)).thenReturn(course1);

        // 执行请求并验证结果
        mockMvc.perform(get("/course/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.title").value("Java编程基础"))
                .andExpect(jsonPath("$.data.teacherName").value("王教授"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询课程")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(courseService.selectPage(any(Course.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/course/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].title").value("Java编程基础"))
                .andExpect(jsonPath("$.data.list[1].title").value("数据结构与算法"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).selectPage(any(Course.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加课程")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/course/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).insert(any(Course.class));
    }

    @Test
    @DisplayName("测试更新课程")
    void testUpdate() throws Exception {
        // 修改课程信息
        course1.setTitle("Java编程基础(已更新)");
        course1.setDescription("本课程介绍Java编程的基础知识，包括语法、面向对象等内容");

        // 执行请求并验证结果
        mockMvc.perform(put("/course/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).update(any(Course.class));
    }

    @Test
    @DisplayName("测试删除课程")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/course/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(courseService, times(1)).deleteById(1L);
    }
} 