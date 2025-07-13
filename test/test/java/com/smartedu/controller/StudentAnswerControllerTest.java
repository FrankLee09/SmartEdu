package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.StudentAnswer;
import com.smartedu.mapper.StudentAnswerMapper;
import com.smartedu.service.StudentAnswerService;
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

@WebMvcTest(StudentAnswerController.class)
@Import(TestConfig.class)
public class StudentAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentAnswerService studentAnswerService;
    
    @MockBean
    private StudentAnswerMapper studentAnswerMapper;

    private StudentAnswer studentAnswer1;
    private StudentAnswer studentAnswer2;
    private List<StudentAnswer> studentAnswerList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        studentAnswer1 = new StudentAnswer();
        studentAnswer1.setId(1L);
        studentAnswer1.setStudentId(101L);
        studentAnswer1.setQuestionId(201L);
        studentAnswer1.setExamId(301L);
        studentAnswer1.setGetscore(8.5);
        studentAnswer1.setAnswer("这是第一个问题的答案");

        studentAnswer2 = new StudentAnswer();
        studentAnswer2.setId(2L);
        studentAnswer2.setStudentId(102L);
        studentAnswer2.setQuestionId(202L);
        studentAnswer2.setExamId(301L);
        studentAnswer2.setGetscore(9.0);
        studentAnswer2.setAnswer("这是第二个问题的答案");

        studentAnswerList = Arrays.asList(studentAnswer1, studentAnswer2);
    }

    @Test
    @DisplayName("测试获取所有学生答案")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(studentAnswerService.getAllAnswers()).thenReturn(studentAnswerList);

        // 执行请求并验证结果
        mockMvc.perform(get("/studentAnswer/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].studentId").value(101))
                .andExpect(jsonPath("$.data[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).getAllAnswers();
    }

    @Test
    @DisplayName("测试根据条件查询学生答案")
    void testSelectAll() throws Exception {
        // 创建查询条件
        StudentAnswer queryStudentAnswer = new StudentAnswer();
        queryStudentAnswer.setStudentId(101L);

        // 模拟Service行为
        when(studentAnswerService.selectAll(any(StudentAnswer.class))).thenReturn(Collections.singletonList(studentAnswer1));

        // 执行请求并验证结果
        mockMvc.perform(post("/studentAnswer/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryStudentAnswer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].getscore").value(8.5));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).selectAll(any(StudentAnswer.class));
    }

    @Test
    @DisplayName("测试根据ID查询学生答案")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(studentAnswerService.selectById(1L)).thenReturn(studentAnswer1);

        // 执行请求并验证结果
        mockMvc.perform(get("/studentAnswer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.studentId").value(101))
                .andExpect(jsonPath("$.data.answer").value("这是第一个问题的答案"));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加学生答案")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/studentAnswer/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAnswer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).add(any(StudentAnswer.class));
    }

    @Test
    @DisplayName("测试更新学生答案")
    void testUpdate() throws Exception {
        // 修改学生答案信息
        studentAnswer1.setGetscore(9.5);
        studentAnswer1.setAnswer("这是更新后的答案");

        // 执行请求并验证结果
        mockMvc.perform(put("/studentAnswer/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAnswer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).update(any(StudentAnswer.class));
    }

    @Test
    @DisplayName("测试删除学生答案")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/studentAnswer/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentAnswerService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量添加学生答案")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/studentAnswer/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAnswerList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个学生答案一次）
        verify(studentAnswerService, times(2)).add(any(StudentAnswer.class));
    }
} 