package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Question;
import com.smartedu.mapper.QuestionMapper;
import com.smartedu.service.QuestionService;
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

@WebMvcTest(QuestionController.class)
@Import(TestConfig.class)
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionService questionService;
    
    @MockBean
    private QuestionMapper questionMapper;

    private Question question1;
    private Question question2;
    private List<Question> questionList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        question1 = new Question();
        question1.setId(1L);
        question1.setTitle("Java基础问题");
        question1.setDescription("什么是Java虚拟机？");
        question1.setCourseId(101L);
        question1.setSelectA("选项A");
        question1.setSelectB("选项B");
        question1.setSelectC("选项C");
        question1.setSelectD("选项D");
        question1.setAnswer("B");
        question1.setKgPoint("Java虚拟机");
        question1.setTag("选择题");
        question1.setScore(5.0);

        question2 = new Question();
        question2.setId(2L);
        question2.setTitle("数据库问题");
        question2.setDescription("什么是SQL注入？");
        question2.setCourseId(102L);
        question2.setAnswer("SQL注入是一种常见的网络攻击方式...");
        question2.setKgPoint("数据库安全");
        question2.setTag("简答题");
        question2.setScore(10.0);

        questionList = Arrays.asList(question1, question2);
    }

    @Test
    @DisplayName("测试获取所有问题")
    void testGetAll() throws Exception {
        // 模拟Service行为
        when(questionService.getAllQuestions()).thenReturn(questionList);

        // 执行请求并验证结果
        mockMvc.perform(get("/question/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java基础问题"))
                .andExpect(jsonPath("$.data[1].title").value("数据库问题"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).getAllQuestions();
    }

    @Test
    @DisplayName("测试根据条件查询问题")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Question queryQuestion = new Question();
        queryQuestion.setTag("选择题");

        // 模拟Service行为
        when(questionService.selectAll(any(Question.class))).thenReturn(Collections.singletonList(question1));

        // 执行请求并验证结果
        mockMvc.perform(post("/question/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryQuestion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java基础问题"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).selectAll(any(Question.class));
    }

    @Test
    @DisplayName("测试根据ID查询问题")
    void testGetById() throws Exception {
        // 模拟Service行为
        when(questionService.selectById(1L)).thenReturn(question1);

        // 执行请求并验证结果
        mockMvc.perform(get("/question/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.title").value("Java基础问题"))
                .andExpect(jsonPath("$.data.description").value("什么是Java虚拟机？"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加问题")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/question/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).insert(any(Question.class));
    }

    @Test
    @DisplayName("测试更新问题")
    void testUpdate() throws Exception {
        // 修改问题信息
        question1.setTitle("Java基础问题(已更新)");
        question1.setDescription("什么是JVM？");

        // 执行请求并验证结果
        mockMvc.perform(put("/question/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(question1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).update(any(Question.class));
    }

    @Test
    @DisplayName("测试删除问题")
    void testDelete() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/question/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试获取随机问题")
    void testGetRandomQuestions() throws Exception {
        // 模拟Service行为
        when(questionService.getRandomQuestions(eq(101L), eq("Java虚拟机"), eq(2), eq(1)))
                .thenReturn(Arrays.asList(question1, question2));

        // 执行请求并验证结果
        mockMvc.perform(get("/question/random")
                        .param("courseId", "101")
                        .param("kgPoint", "Java虚拟机")
                        .param("selectCount", "2")
                        .param("shortAnswerCount", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].title").value("Java基础问题"))
                .andExpect(jsonPath("$.data[1].title").value("数据库问题"));

        // 验证service方法是否被调用
        verify(questionService, times(1)).getRandomQuestions(eq(101L), eq("Java虚拟机"), eq(2), eq(1));
    }

    @Test
    @DisplayName("测试批量添加问题")
    void testAddJson() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/question/addJson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用两次（每个问题一次）
        verify(questionService, times(2)).insert(any(Question.class));
    }
} 