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

/**
 * DifyRecommendService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class DifyRecommendServiceTest {

    @InjectMocks
    private DifyRecommendService difyRecommendService;

    private String testQuery;
    private String testResponse;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testQuery = "Java编程基础";
        
        // 模拟API返回的JSON响应
        testResponse = "{\n" +
                "  \"data\": {\n" +
                "    \"outputs\": {\n" +
                "      \"text\": \"推荐学习内容：Java语法基础、面向对象编程、集合框架\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        
        // 设置私有静态常量
        ReflectionTestUtils.setField(difyRecommendService, "API_URL", "http://localhost/v1/workflows/run");
        ReflectionTestUtils.setField(difyRecommendService, "API_KEY", "test-api-key");
    }

    @Test
    @DisplayName("测试成功获取学习推荐")
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
            when(connection.getResponseCode()).thenReturn(200);
            
            // 模拟输出流
            when(connection.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
            
            // 模拟输入流返回测试响应
            when(connection.getInputStream()).thenReturn(new ByteArrayInputStream(testResponse.getBytes()));
            
            // 执行测试
            String result = difyRecommendService.runWorkflow(testQuery);
            
            // 验证结果
            assertNotNull(result);
            assertEquals(testResponse, result);
        }
    }

    @Test
    @DisplayName("测试获取学习推荐失败 - HTTP错误")
    void testRunWorkflow_HttpError() throws Exception {
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
            when(connection.getResponseCode()).thenReturn(400);
            
            // 模拟输出流
            when(connection.getOutputStream()).thenReturn(mock(java.io.OutputStream.class));
            
            // 模拟错误流
            when(connection.getErrorStream()).thenReturn(new ByteArrayInputStream("{\"error\":\"Bad Request\"}".getBytes()));
            
            // 执行测试并验证异常
            Exception exception = assertThrows(RuntimeException.class, () -> {
                difyRecommendService.runWorkflow(testQuery);
            });
            
            assertTrue(exception.getMessage().contains("请求失败"));
        }
    }

    @Test
    @DisplayName("测试获取学习推荐失败 - 网络错误")
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
            when(connection.getResponseCode()).thenThrow(new IOException("网络连接错误"));
            
            // 执行测试并验证异常
            Exception exception = assertThrows(RuntimeException.class, () -> {
                difyRecommendService.runWorkflow(testQuery);
            });
            
            assertTrue(exception.getMessage().contains("请求失败"));
        }
    }
} 