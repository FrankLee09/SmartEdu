package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Submission;
import com.smartedu.mapper.SubmissionMapper;
import com.smartedu.service.SubmissionService;
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

@WebMvcTest(SubmissionController.class)
@Import(TestConfig.class)
public class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SubmissionService submissionService;
    
    @MockBean
    private SubmissionMapper submissionMapper;

    private Submission submission1;
    private Submission submission2;
    private List<Submission> submissionList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        submission1 = new Submission();
        submission1.setId(1L);
        submission1.setStudentId(101L);
        submission1.setTaskId(201L);
        submission1.setSubmitTime("2023-12-15 10:30:00");
        submission1.setFilename("作业1.pdf");
        submission1.setFileUrl("http://example.com/submission1.pdf");

        submission2 = new Submission();
        submission2.setId(2L);
        submission2.setStudentId(102L);
        submission2.setTaskId(202L);
        submission2.setSubmitTime("2023-12-16 14:45:00");
        submission2.setFilename("作业2.pdf");
        submission2.setFileUrl("http://example.com/submission2.pdf");

        submissionList = Arrays.asList(submission1, submission2);
    }

    @Test
    @DisplayName("测试获取所有提交")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(submissionService.getAllSubmissions()).thenReturn(submissionList);

        // 执行请求并验证结果
        mockMvc.perform(get("/submission/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].studentId").value(101))
                .andExpect(jsonPath("$.data[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).getAllSubmissions();
    }

    @Test
    @DisplayName("测试根据条件查询提交")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Submission querySubmission = new Submission();
        querySubmission.setStudentId(101L);

        // 模拟Service行为
        when(submissionService.selectAll(any(Submission.class))).thenReturn(Collections.singletonList(submission1));

        // 执行请求并验证结果
        mockMvc.perform(post("/submission/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(querySubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].taskId").value(201));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).selectAll(any(Submission.class));
    }

    @Test
    @DisplayName("测试根据ID查询提交")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(submissionService.selectById(1L)).thenReturn(submission1);

        // 执行请求并验证结果
        mockMvc.perform(get("/submission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.studentId").value(101))
                .andExpect(jsonPath("$.data.filename").value("作业1.pdf"));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加提交")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/submission/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).addSubmission(any(Submission.class));
    }

    @Test
    @DisplayName("测试更新提交")
    void testUpdate() throws Exception {
        // 修改提交信息
        submission1.setFilename("更新后的作业1.pdf");

        // 执行请求并验证结果
        mockMvc.perform(put("/submission/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submission1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).updateSubmission(any(Submission.class));
    }

    @Test
    @DisplayName("测试删除提交")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/submission/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(submissionService, times(1)).deleteById(1L);
    }
} 