package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Task;
import com.smartedu.mapper.TaskMapper;
import com.smartedu.service.TaskService;
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

@WebMvcTest(TaskController.class)
@Import(TestConfig.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;
    
    @MockBean
    private TaskMapper taskMapper;

    private Task task1;
    private Task task2;
    private List<Task> taskList;
    private PageInfo<Task> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Java编程作业");
        task1.setContent("完成Java基础练习题");
        task1.setDueDate("2023-12-31");
        task1.setClassIds(Collections.singletonList(1L));
        task1.setCourseId(101L);
        task1.setTime("2023-12-01");

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("数据库设计作业");
        task2.setContent("完成数据库ER图设计");
        task2.setDueDate("2023-12-25");
        task2.setClassIds(Collections.singletonList(2L));
        task2.setCourseId(102L);
        task2.setTime("2023-12-05");

        taskList = Arrays.asList(task1, task2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(taskList);
    }

    @Test
    @DisplayName("测试获取所有任务")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(taskService.getAllTasks()).thenReturn(taskList);

        // 执行请求并验证结果
        mockMvc.perform(get("/task/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java编程作业"))
                .andExpect(jsonPath("$.data[1].title").value("数据库设计作业"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("测试根据ID查询任务")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(taskService.selectById(1L)).thenReturn(task1);

        // 执行请求并验证结果
        mockMvc.perform(get("/task/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.title").value("Java编程作业"))
                .andExpect(jsonPath("$.data.content").value("完成Java基础练习题"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询任务")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(taskService.selectPage(any(Task.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/task/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].title").value("Java编程作业"))
                .andExpect(jsonPath("$.data.list[1].title").value("数据库设计作业"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).selectPage(any(Task.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加任务")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).addTaskJson(any(Task.class));
    }

    @Test
    @DisplayName("测试更新任务")
    void testUpdate() throws Exception {
        // 修改任务信息
        task1.setTitle("Java编程作业(已更新)");
        task1.setContent("完成Java高级练习题");

        // 执行请求并验证结果
        mockMvc.perform(put("/task/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).updateTask(any(Task.class));
    }

    @Test
    @DisplayName("测试删除任务")
    void testDeleteById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/task/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除任务")
    void testDeleteBatch() throws Exception {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 执行请求并验证结果
        mockMvc.perform(delete("/task/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(taskService, times(1)).deleteBatch(ids);
    }

    @Test
    @DisplayName("测试批量添加任务")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/task/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个任务一次）
        verify(taskService, times(2)).addTask(any(Task.class));
    }
} 