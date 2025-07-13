package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Exam;
import com.smartedu.mapper.ExamMapper;
import com.smartedu.service.ExamService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamController.class)
@Import(TestConfig.class)
public class ExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExamService examService;
    
    @MockBean
    private ExamMapper examMapper;

    private Exam exam1;
    private Exam exam2;
    private List<Exam> examList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        exam1 = new Exam();
        exam1.setId(1L);
        exam1.setTitle("Java期中考试");
        exam1.setCourseId(101L);
        exam1.setStartTime("2023-10-15 09:00:00");
        exam1.setEndTime("2023-10-15 11:00:00");
        exam1.setTotalScore(100.0);
        exam1.setQuestionIds(Arrays.asList(1L, 2L, 3L));
        exam1.setTag("期中考试");

        exam2 = new Exam();
        exam2.setId(2L);
        exam2.setTitle("数据库期末考试");
        exam2.setCourseId(102L);
        exam2.setStartTime("2023-12-20 14:00:00");
        exam2.setEndTime("2023-12-20 16:00:00");
        exam2.setTotalScore(100.0);
        exam2.setQuestionIds(Arrays.asList(4L, 5L, 6L));
        exam2.setTag("期末考试");

        examList = Arrays.asList(exam1, exam2);
    }

    @Test
    @DisplayName("测试获取所有考试")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(examService.getAllExams()).thenReturn(examList);

        // 执行请求并验证结果
        mockMvc.perform(get("/exam/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java期中考试"))
                .andExpect(jsonPath("$.data[1].title").value("数据库期末考试"));

        // 验证service方法是否被调用
        verify(examService, times(1)).getAllExams();
    }

    @Test
    @DisplayName("测试根据条件查询考试")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Exam queryExam = new Exam();
        queryExam.setTag("期中考试");

        // 模拟Service行为
        when(examService.selectAll(any(Exam.class))).thenReturn(Collections.singletonList(exam1));

        // 执行请求并验证结果
        mockMvc.perform(post("/exam/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryExam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java期中考试"));

        // 验证service方法是否被调用
        verify(examService, times(1)).selectAll(any(Exam.class));
    }

    @Test
    @DisplayName("测试根据ID查询考试")
    void testGetById() throws Exception {
        // 模拟Service行为
        when(examService.selectById(1L)).thenReturn(exam1);

        // 执行请求并验证结果
        mockMvc.perform(get("/exam/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.title").value("Java期中考试"))
                .andExpect(jsonPath("$.data.courseId").value(101));

        // 验证service方法是否被调用
        verify(examService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加考试")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/exam/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exam1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examService, times(1)).insert(any(Exam.class));
    }

    @Test
    @DisplayName("测试更新考试")
    void testUpdate() throws Exception {
        // 修改考试信息
        exam1.setTitle("Java期中考试(已更新)");
        exam1.setEndTime("2023-10-15 12:00:00");

        // 执行请求并验证结果
        mockMvc.perform(put("/exam/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exam1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examService, times(1)).update(any(Exam.class));
    }

    @Test
    @DisplayName("测试删除考试")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/exam/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量添加考试")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/exam/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个考试一次）
        verify(examService, times(2)).insert(any(Exam.class));
    }
} 