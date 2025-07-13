package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Resourze;
import com.smartedu.mapper.ResourzeMapper;
import com.smartedu.service.ResourzeService;
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

@WebMvcTest(ResourzeController.class)
@Import(TestConfig.class)
public class ResourzeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourzeService resourzeService;
    
    @MockBean
    private ResourzeMapper resourzeMapper;

    private Resourze resourze1;
    private Resourze resourze2;
    private List<Resourze> resourzeList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        resourze1 = new Resourze();
        resourze1.setId(1L);
        resourze1.setTitle("Java编程基础");
        resourze1.setTag("文档");
        resourze1.setFileUrl("http://example.com/java-basics.pdf");
        resourze1.setCourseId(101L);
        resourze1.setUploadTime("2023-12-15");

        resourze2 = new Resourze();
        resourze2.setId(2L);
        resourze2.setTitle("数据结构与算法");
        resourze2.setTag("视频");
        resourze2.setFileUrl("http://example.com/algorithms.mp4");
        resourze2.setCourseId(102L);
        resourze2.setUploadTime("2023-12-16");

        resourzeList = Arrays.asList(resourze1, resourze2);
    }

    @Test
    @DisplayName("测试获取所有资源")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(resourzeService.getAllResources()).thenReturn(resourzeList);

        // 执行请求并验证结果
        mockMvc.perform(get("/resourze/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java编程基础"))
                .andExpect(jsonPath("$.data[1].title").value("数据结构与算法"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).getAllResources();
    }

    @Test
    @DisplayName("测试根据条件查询资源")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Resourze queryResourze = new Resourze();
        queryResourze.setTag("文档");

        // 模拟Service行为
        when(resourzeService.selectAll(any(Resourze.class))).thenReturn(Collections.singletonList(resourze1));

        // 执行请求并验证结果
        mockMvc.perform(post("/resourze/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryResourze)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java编程基础"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).selectAll(any(Resourze.class));
    }

    @Test
    @DisplayName("测试根据ID查询资源")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(resourzeService.selectById(1L)).thenReturn(resourze1);

        // 执行请求并验证结果
        mockMvc.perform(get("/resourze/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.title").value("Java编程基础"))
                .andExpect(jsonPath("$.data.tag").value("文档"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加资源")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/resourze/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourze1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).insert(any(Resourze.class));
    }

    @Test
    @DisplayName("测试更新资源")
    void testUpdate() throws Exception {
        // 修改资源信息
        resourze1.setTag("更新后的文档");

        // 执行请求并验证结果
        mockMvc.perform(put("/resourze/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resourze1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).update(any(Resourze.class));
    }

    @Test
    @DisplayName("测试删除资源")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/resourze/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(resourzeService, times(1)).deleteById(1L);
    }
} 