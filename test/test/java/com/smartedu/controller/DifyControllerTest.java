package com.smartedu.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.controller.DifyController.KnowledgeGraphRequest;
import com.smartedu.controller.DifyController.QuestionScoreRequest;
import com.smartedu.entity.Exam;
import com.smartedu.entity.File;
import com.smartedu.entity.Question;
import com.smartedu.entity.StudentAnswer;
import com.smartedu.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DifyController.class)
@Import(TestConfig.class)
public class DifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DifyQuestionScoreService difyQuestionScoreService;

    @MockBean
    private StudentAnswerService studentAnswerService;

    @MockBean
    private DifyKnowledgeGraphService difyKnowledgeGraphService;

    @MockBean
    private DifyRecommendService difyRecommendService;

    @MockBean
    private DifyReportScoreService difyReportScoreService;

    private Question testQuestion;
    private Exam testExam;
    private JsonNode testGraph;
    private String testKnowledge;

    @BeforeEach
    void setUp() throws Exception {
        // 准备测试数据
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setDescription("测试题目描述");
        testQuestion.setAnswer("标准答案");
        testQuestion.setScore(10.0);

        testExam = new Exam();
        testExam.setId(1L);
        testExam.setTitle("测试考试");

        testKnowledge = "Java编程基础";

        // 创建测试知识图谱JSON
        String graphJson = "{\"nodes\":[{\"id\":1,\"label\":\"Java\"}],\"edges\":[]}";
        testGraph = objectMapper.readTree(graphJson);
    }

    @Test
    @DisplayName("测试知识图谱生成")
    void testKnowledgeGraph() throws Exception {
        // 创建请求对象
        KnowledgeGraphRequest request = new KnowledgeGraphRequest();
        request.setKnowledge(testKnowledge);

        // 模拟Service行为
        when(difyKnowledgeGraphService.runWorkflow(testKnowledge)).thenReturn(testGraph);

        // 执行请求并验证结果
        mockMvc.perform(post("/dify/knowledgeGraph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.nodes[0].label").value("Java"));

        // 验证service方法是否被调用
        verify(difyKnowledgeGraphService, times(1)).runWorkflow(testKnowledge);
    }

    @Test
    @DisplayName("测试知识图谱生成 - 空知识点")
    void testKnowledgeGraph_EmptyKnowledge() throws Exception {
        // 创建请求对象
        KnowledgeGraphRequest request = new KnowledgeGraphRequest();
        request.setKnowledge("");

        // 执行请求并验证结果
        mockMvc.perform(post("/dify/knowledgeGraph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("知识点内容不能为空"));

        // 验证service方法没有被调用
        verify(difyKnowledgeGraphService, never()).runWorkflow(anyString());
    }

    @Test
    @DisplayName("测试问题评分")
    void testQuestionScore() throws Exception {
        // 创建请求对象
        QuestionScoreRequest request = new QuestionScoreRequest();
        request.setTheQuestion(testQuestion);
        request.setExam(testExam);
        request.setStudentId(101L);
        request.setSolution("学生答案");

        // 模拟Service行为
        when(difyQuestionScoreService.runWorkflow(
                eq("测试题目描述"), 
                eq("学生答案"), 
                eq("标准答案"), 
                eq("10.0")
        )).thenReturn("评分结果: 8");

        // 执行请求并验证结果
        mockMvc.perform(post("/dify/questionScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(difyQuestionScoreService, times(1)).runWorkflow(
                eq("测试题目描述"), 
                eq("学生答案"), 
                eq("标准答案"), 
                eq("10.0")
        );
        
        // 验证studentAnswerService.add方法是否被调用，并且参数正确
        verify(studentAnswerService, times(1)).add(any(StudentAnswer.class));
    }

    @Test
    @DisplayName("测试问题评分 - 大模型错误")
    void testQuestionScore_ModelError() throws Exception {
        // 创建请求对象
        QuestionScoreRequest request = new QuestionScoreRequest();
        request.setTheQuestion(testQuestion);
        request.setExam(testExam);
        request.setStudentId(101L);
        request.setSolution("学生答案");

        // 模拟Service行为 - 返回无法解析的结果
        when(difyQuestionScoreService.runWorkflow(
                eq("测试题目描述"), 
                eq("学生答案"), 
                eq("标准答案"), 
                eq("10.0")
        )).thenReturn("评分结果: 无法评分");

        // 执行请求并验证结果
        mockMvc.perform(post("/dify/questionScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("大模型错误"));

        // 验证service方法是否被调用
        verify(difyQuestionScoreService, times(1)).runWorkflow(
                eq("测试题目描述"), 
                eq("学生答案"), 
                eq("标准答案"), 
                eq("10.0")
        );
        
        // 验证studentAnswerService.add方法没有被调用
        verify(studentAnswerService, never()).add(any(StudentAnswer.class));
    }

    @Test
    @DisplayName("测试报告评分")
    void testReportScore() throws Exception {
        // 创建模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "测试报告内容".getBytes()
        );

        // 模拟Service行为
        when(difyReportScoreService.uploadFile(anyString(), eq("1"))).thenReturn("file-123");
        when(difyReportScoreService.runWorkflow(eq("file-123"), eq("1"))).thenReturn("评分结果: 90分");

        // 执行请求并验证结果
        mockMvc.perform(multipart("/dify/reportScore")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").value("评分结果: 90分"));

        // 验证service方法是否被调用
        verify(difyReportScoreService, times(1)).uploadFile(anyString(), eq("1"));
        verify(difyReportScoreService, times(1)).runWorkflow(eq("file-123"), eq("1"));
    }

    @Test
    @DisplayName("测试报告评分 - 上传失败")
    void testReportScore_UploadFailed() throws Exception {
        // 创建模拟文件
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "测试报告内容".getBytes()
        );

        // 模拟Service行为 - 文件上传失败
        when(difyReportScoreService.uploadFile(anyString(), eq("1"))).thenReturn(null);

        // 执行请求并验证结果
        mockMvc.perform(multipart("/dify/reportScore")
                        .file(file)
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("文件上传失败"));

        // 验证service方法是否被调用
        verify(difyReportScoreService, times(1)).uploadFile(anyString(), eq("1"));
        verify(difyReportScoreService, never()).runWorkflow(anyString(), anyString());
    }

    @Test
    @DisplayName("测试获取学习推荐")
    void testGetRecommendation() throws Exception {
        // 模拟Service行为
        when(difyRecommendService.getRecommendation(101L)).thenReturn("推荐学习Java基础和数据结构");

        // 执行请求并验证结果
        mockMvc.perform(get("/dify/recommend/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").value("推荐学习Java基础和数据结构"));

        // 验证service方法是否被调用
        verify(difyRecommendService, times(1)).getRecommendation(101L);
    }

    @Test
    @DisplayName("测试获取学习推荐 - 失败")
    void testGetRecommendation_Failed() throws Exception {
        // 模拟Service行为
        when(difyRecommendService.getRecommendation(101L)).thenReturn("解析推荐内容失败");

        // 执行请求并验证结果
        mockMvc.perform(get("/dify/recommend/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("无法生成学习建议，请稍后再试"));

        // 验证service方法是否被调用
        verify(difyRecommendService, times(1)).getRecommendation(101L);
    }
} 