package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Teacher;
import com.smartedu.mapper.TeacherMapper;
import com.smartedu.service.TeacherService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
@Import(TestConfig.class)
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeacherService teacherService;
    
    @MockBean
    private TeacherMapper teacherMapper;

    private Teacher teacher1;
    private Teacher teacher2;
    private List<Teacher> teacherList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setName("张教授");
        teacher1.setEmail("zhang@example.com");
        teacher1.setUsername("zhang");
        teacher1.setPassword("password123");
        teacher1.setRole("teacher");

        teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setName("李教授");
        teacher2.setEmail("li@example.com");
        teacher2.setUsername("li");
        teacher2.setPassword("password456");
        teacher2.setRole("teacher");

        teacherList = Arrays.asList(teacher1, teacher2);
    }

    @Test
    @DisplayName("测试获取所有教师")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(teacherService.getAllTeachers()).thenReturn(teacherList);

        // 执行请求并验证结果
        mockMvc.perform(get("/teacher/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].name").value("张教授"))
                .andExpect(jsonPath("$.data[1].name").value("李教授"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).getAllTeachers();
    }

    @Test
    @DisplayName("测试根据条件查询教师")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Teacher queryTeacher = new Teacher();
        queryTeacher.setName("张");

        // 模拟Service行为
        when(teacherService.selectAll(any(Teacher.class))).thenReturn(Arrays.asList(teacher1));

        // 执行请求并验证结果
        mockMvc.perform(post("/teacher/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryTeacher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].name").value("张教授"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).selectAll(any(Teacher.class));
    }

    @Test
    @DisplayName("测试根据ID查询教师")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(teacherService.selectById(1L)).thenReturn(teacher1);

        // 执行请求并验证结果
        mockMvc.perform(get("/teacher/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.name").value("张教授"))
                .andExpect(jsonPath("$.data.email").value("zhang@example.com"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加教师")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/teacher/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).insert(any(Teacher.class));
    }

    @Test
    @DisplayName("测试更新教师")
    void testUpdate() throws Exception {
        // 修改教师信息
        teacher1.setName("张教授(已更新)");
        teacher1.setEmail("zhang_updated@example.com");

        // 执行请求并验证结果
        mockMvc.perform(put("/teacher/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).update(any(Teacher.class));
    }

    @Test
    @DisplayName("测试删除教师")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/teacher/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量添加教师")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/teacher/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个教师一次）
        verify(teacherService, times(2)).insert(any(Teacher.class));
    }
} 