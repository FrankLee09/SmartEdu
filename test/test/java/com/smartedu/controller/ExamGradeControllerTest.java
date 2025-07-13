package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.ExamGrade;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.ExamGradeMapper;
import com.smartedu.service.ExamGradeService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamGradeController.class)
@Import(TestConfig.class)
public class ExamGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExamGradeService examGradeService;
    
    @MockBean
    private ExamGradeMapper examGradeMapper;

    private ExamGrade examGrade1;
    private ExamGrade examGrade2;
    private TaskGrade taskGrade;
    private List<ExamGrade> examGradeList;
    private PageInfo<ExamGrade> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        examGrade1 = new ExamGrade();
        examGrade1.setId(1L);
        examGrade1.setStudentId(101L);
        examGrade1.setExamId(201L);
        examGrade1.setScore(85.5);
        examGrade1.setGradedate("2023-12-15");

        examGrade2 = new ExamGrade();
        examGrade2.setId(2L);
        examGrade2.setStudentId(102L);
        examGrade2.setExamId(202L);
        examGrade2.setScore(92.0);
        examGrade2.setGradedate("2023-12-16");

        examGradeList = Arrays.asList(examGrade1, examGrade2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(examGradeList);
        
        // 准备TaskGrade测试数据（用于selectById方法）
        taskGrade = new TaskGrade();
        taskGrade.setId(1L);
        taskGrade.setStudentId(101L);
        taskGrade.setTaskId(201L);
        taskGrade.setScore(85.5);
        taskGrade.setGradedate("2023-12-15");
    }

    @Test
    @DisplayName("测试获取所有考试成绩")
    void testGetAllGrades() throws Exception {
        // 模拟Service行为
        when(examGradeService.getAllGrades()).thenReturn(examGradeList);

        // 执行请求并验证结果
        mockMvc.perform(get("/examgrade/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].studentId").value(101))
                .andExpect(jsonPath("$.data[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).getAllGrades();
    }

    @Test
    @DisplayName("测试根据条件查询考试成绩")
    void testSelectAllGrades() throws Exception {
        // 创建查询条件
        ExamGrade queryExamGrade = new ExamGrade();
        queryExamGrade.setStudentId(101L);

        // 模拟Service行为
        when(examGradeService.selectAll(any(ExamGrade.class))).thenReturn(Collections.singletonList(examGrade1));

        // 执行请求并验证结果
        mockMvc.perform(get("/examgrade/selectAll")
                        .param("studentId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].score").value(85.5));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).selectAll(any(ExamGrade.class));
    }

    @Test
    @DisplayName("测试根据ID查询成绩")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(examGradeService.selectById(1L)).thenReturn(taskGrade);

        // 执行请求并验证结果
        mockMvc.perform(get("/examgrade/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.studentId").value(101))
                .andExpect(jsonPath("$.data.score").value(85.5));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询考试成绩")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(examGradeService.selectPage(any(ExamGrade.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/examgrade/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].studentId").value(101))
                .andExpect(jsonPath("$.data.list[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).selectPage(any(ExamGrade.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加考试成绩")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/examgrade/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examGrade1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).addGrade(any(ExamGrade.class));
    }

    @Test
    @DisplayName("测试更新考试成绩")
    void testUpdate() throws Exception {
        // 修改考试成绩信息
        examGrade1.setScore(90.0);

        // 执行请求并验证结果
        mockMvc.perform(put("/examgrade/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examGrade1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).updateGrade(any(ExamGrade.class));
    }

    @Test
    @DisplayName("测试删除考试成绩")
    void testDeleteById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/examgrade/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除考试成绩")
    void testDeleteBatch() throws Exception {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 执行请求并验证结果
        mockMvc.perform(delete("/examgrade/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(examGradeService, times(1)).deleteBatch(ids);
    }

    @Test
    @DisplayName("测试批量添加考试成绩")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/examgrade/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(examGradeList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个考试成绩一次）
        verify(examGradeService, times(2)).addGrade(any(ExamGrade.class));
    }
} 