package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Clazz;
import com.smartedu.mapper.ClazzMapper;
import com.smartedu.service.ClazzService;
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

@WebMvcTest(ClazzController.class)
@Import(TestConfig.class)
public class ClazzControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClazzService classService;
    
    @MockBean
    private ClazzMapper clazzMapper;

    private Clazz clazz1;
    private Clazz clazz2;
    private List<Clazz> clazzList;
    private PageInfo<Clazz> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        clazz1 = new Clazz();
        clazz1.setId(1L);
        clazz1.setName("计算机科学1班");
        clazz1.setTeacherId(101L);

        clazz2 = new Clazz();
        clazz2.setId(2L);
        clazz2.setName("软件工程2班");
        clazz2.setTeacherId(102L);

        clazzList = Arrays.asList(clazz1, clazz2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(clazzList);
    }

    @Test
    @DisplayName("测试获取所有班级")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(classService.getAllclasses()).thenReturn(clazzList);

        // 执行请求并验证结果
        mockMvc.perform(get("/class/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].name").value("计算机科学1班"))
                .andExpect(jsonPath("$.data[1].name").value("软件工程2班"));

        // 验证service方法是否被调用
        verify(classService, times(1)).getAllclasses();
    }

    @Test
    @DisplayName("测试根据ID查询班级")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(classService.selectById(1L)).thenReturn(clazz1);

        // 执行请求并验证结果
        mockMvc.perform(get("/class/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.name").value("计算机科学1班"))
                .andExpect(jsonPath("$.data.teacherId").value(101));

        // 验证service方法是否被调用
        verify(classService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询班级")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(classService.selectPage(any(Clazz.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/class/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].name").value("计算机科学1班"))
                .andExpect(jsonPath("$.data.list[1].name").value("软件工程2班"));

        // 验证service方法是否被调用
        verify(classService, times(1)).selectPage(any(Clazz.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加班级")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/class/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clazz1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(classService, times(1)).addClazz(any(Clazz.class));
    }

    @Test
    @DisplayName("测试更新班级")
    void testUpdate() throws Exception {
        // 修改班级信息
        clazz1.setName("计算机科学1班(已更新)");

        // 执行请求并验证结果
        mockMvc.perform(put("/class/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clazz1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(classService, times(1)).updateClazz(any(Clazz.class));
    }

    @Test
    @DisplayName("测试删除班级")
    void testDeleteById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/class/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(classService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除班级")
    void testDeleteBatch() throws Exception {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 执行请求并验证结果
        mockMvc.perform(delete("/class/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(classService, times(1)).deleteBatch(ids);
    }

    @Test
    @DisplayName("测试批量添加班级")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/class/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clazzList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个班级一次）
        verify(classService, times(2)).addClazz(any(Clazz.class));
    }
} 