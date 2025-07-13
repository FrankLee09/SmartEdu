package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.TaskGrade;
import com.smartedu.mapper.TaskGradeMapper;
import com.smartedu.service.TaskGradeService;
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

@WebMvcTest(TaskGradeController.class)
@Import(TestConfig.class)
public class TaskGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskGradeService taskGradeService;
    
    @MockBean
    private TaskGradeMapper taskGradeMapper;

    private TaskGrade taskGrade1;
    private TaskGrade taskGrade2;
    private List<TaskGrade> taskGradeList;
    private PageInfo<TaskGrade> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        taskGrade1 = new TaskGrade();
        taskGrade1.setId(1L);
        taskGrade1.setStudentId(101L);
        taskGrade1.setTaskId(201L);
        taskGrade1.setScore(85.5);
        taskGrade1.setGradedate("2023-12-15");

        taskGrade2 = new TaskGrade();
        taskGrade2.setId(2L);
        taskGrade2.setStudentId(102L);
        taskGrade2.setTaskId(202L);
        taskGrade2.setScore(92.0);
        taskGrade2.setGradedate("2023-12-16");

        taskGradeList = Arrays.asList(taskGrade1, taskGrade2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(taskGradeList);
    }

    @Test
    @DisplayName("测试获取所有任务成绩")
    void testGetAllGrades() throws Exception {
        // 模拟Service行为
        when(taskGradeService.getAllGrades()).thenReturn(taskGradeList);

        // 执行请求并验证结果
        mockMvc.perform(get("/taskgrade/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].studentId").value(101))
                .andExpect(jsonPath("$.data[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).getAllGrades();
    }

    @Test
    @DisplayName("测试根据条件查询任务成绩")
    void testSelectAllGrades() throws Exception {
        // 创建查询条件
        TaskGrade queryTaskGrade = new TaskGrade();
        queryTaskGrade.setStudentId(101L);

        // 模拟Service行为
        when(taskGradeService.selectAll(any(TaskGrade.class))).thenReturn(Collections.singletonList(taskGrade1));

        // 执行请求并验证结果
        mockMvc.perform(get("/taskgrade/selectAll")
                        .param("studentId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].score").value(85.5));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).selectAll(any(TaskGrade.class));
    }

    @Test
    @DisplayName("测试根据ID查询任务成绩")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(taskGradeService.selectById(1L)).thenReturn(taskGrade1);

        // 执行请求并验证结果
        mockMvc.perform(get("/taskgrade/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.studentId").value(101))
                .andExpect(jsonPath("$.data.score").value(85.5));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询任务成绩")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(taskGradeService.selectPage(any(TaskGrade.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/taskgrade/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].studentId").value(101))
                .andExpect(jsonPath("$.data.list[1].studentId").value(102));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).selectPage(any(TaskGrade.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加任务成绩")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/taskgrade/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskGrade1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).addGrade(any(TaskGrade.class));
    }

    @Test
    @DisplayName("测试更新任务成绩")
    void testUpdate() throws Exception {
        // 修改任务成绩信息
        taskGrade1.setScore(90.0);

        // 执行请求并验证结果
        mockMvc.perform(put("/taskgrade/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskGrade1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).updateGrade(any(TaskGrade.class));
    }

    @Test
    @DisplayName("测试删除任务成绩")
    void testDeleteById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/taskgrade/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除任务成绩")
    void testDeleteBatch() throws Exception {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 执行请求并验证结果
        mockMvc.perform(delete("/taskgrade/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskGradeService, times(1)).deleteBatch(ids);
    }

    @Test
    @DisplayName("测试批量添加任务成绩")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/taskgrade/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskGradeList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个任务成绩一次）
        verify(taskGradeService, times(2)).addGrade(any(TaskGrade.class));
    }
} 