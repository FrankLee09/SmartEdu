package com.smartedu.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DifyKnowledgeGraphServiceTest {

    @InjectMocks
    private DifyKnowledgeGraphService knowledgeGraphService;

    private ObjectMapper objectMapper;
    private String testKnowledge;
    private String testResponse;
    private JsonNode expectedJsonNode;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        testKnowledge = "测试知识点";
        
        // 模拟API返回的JSON响应
        testResponse = "{\n" +
                "  \"data\": {\n" +
                "    \"outputs\": {\n" +
                "      \"text\": \"这里是一些文本{\\\"nodes\\\":[{\\\"id\\\":1,\\\"label\\\":\\\"知识点1\\\"}],\\\"edges\\\":[]}后面的文本\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        
        // 预期解析出的JSON节点
        String expectedJson = "{\"nodes\":[{\"id\":1,\"label\":\"知识点1\"}],\"edges\":[]}";
        expectedJsonNode = objectMapper.readTree(expectedJson);
        
        // 设置私有静态常量
        ReflectionTestUtils.setField(knowledgeGraphService, "API_URL", "http://localhost/v1/workflows/run");
        ReflectionTestUtils.setField(knowledgeGraphService, "API_KEY", "test-api-key");
    }

    @Test
    @DisplayName("测试成功处理知识图谱")
    void testRunWorkflow_Success() throws Exception {
        // 使用MockedStatic模拟URL和HttpURLConnection
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            URL url = mock(URL.class);
            HttpURLConnection connection = mock(HttpURLConnection.class);
            
            mockedUrl.when(() -> new URL(anyString())).thenReturn(url);
            when(url.openConnection()).thenReturn(connection);
            
            // 模拟连接行为
            doNothing().when(connection).setRequestMethod(anyString());
            doNothing().when(connection).setRequestProperty(anyString(), anyString());
            doNothing().when(connection).setDoOutput(anyBoolean());
            
            // 模拟输出流
            when(connection.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
            
            // 模拟输入流返回测试响应
            when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(testResponse.getBytes()));
            
            // 执行测试
            JsonNode result = knowledgeGraphService.runWorkflow(testKnowledge);
            
            // 验证结果
            assertNotNull(result);
            assertTrue(result.has("nodes"));
            assertTrue(result.has("edges"));
            assertEquals(1, result.get("nodes").size());
            assertEquals(0, result.get("edges").size());
            assertEquals("知识点1", result.get("nodes").get(0).get("label").asText());
        }
    }

    @Test
    @DisplayName("测试处理知识图谱失败 - 无效JSON")
    void testRunWorkflow_InvalidJson() throws Exception {
        // 使用MockedStatic模拟URL和HttpURLConnection
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            URL url = mock(URL.class);
            HttpURLConnection connection = mock(HttpURLConnection.class);
            
            mockedUrl.when(() -> new URL(anyString())).thenReturn(url);
            when(url.openConnection()).thenReturn(connection);
            
            // 模拟连接行为
            doNothing().when(connection).setRequestMethod(anyString());
            doNothing().when(connection).setRequestProperty(anyString(), anyString());
            doNothing().when(connection).setDoOutput(anyBoolean());
            
            // 模拟输出流
            when(connection.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
            
            // 模拟输入流返回无效JSON
            String invalidResponse = "{\n" +
                    "  \"data\": {\n" +
                    "    \"outputs\": {\n" +
                    "      \"text\": \"这里没有JSON数据\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(invalidResponse.getBytes()));
            
            // 执行测试并验证异常
            Exception exception = assertThrows(RuntimeException.class, () -> {
                knowledgeGraphService.runWorkflow(testKnowledge);
            });
            
            assertTrue(exception.getMessage().contains("未找到有效的知识图谱数据"));
        }
    }

    @Test
    @DisplayName("测试处理知识图谱失败 - 网络错误")
    void testRunWorkflow_NetworkError() throws Exception {
        // 使用MockedStatic模拟URL和HttpURLConnection
        try (MockedStatic<URL> mockedUrl = Mockito.mockStatic(URL.class)) {
            URL url = mock(URL.class);
            HttpURLConnection connection = mock(HttpURLConnection.class);
            
            mockedUrl.when(() -> new URL(anyString())).thenReturn(url);
            when(url.openConnection()).thenReturn(connection);
            
            // 模拟连接行为
            doNothing().when(connection).setRequestMethod(anyString());
            doNothing().when(connection).setRequestProperty(anyString(), anyString());
            doNothing().when(connection).setDoOutput(anyBoolean());
            
            // 模拟输出流
            when(connection.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
            
            // 模拟网络错误
            when(connection.getInputStream()).thenThrow(new IOException("网络连接错误"));
            
            // 执行测试并验证异常
            Exception exception = assertThrows(RuntimeException.class, () -> {
                knowledgeGraphService.runWorkflow(testKnowledge);
            });
            
            assertTrue(exception.getMessage().contains("处理知识图谱失败"));
        }
    }
} 