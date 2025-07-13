package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.service.FileService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@Import(TestConfig.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileService fileService;

    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "测试文件内容".getBytes()
        );
    }

    @Test
    @DisplayName("测试上传文件")
    void testUpload() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(multipart("/files/upload")
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("测试上传文件（uploadFile方法）")
    void testUploadFile() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(multipart("/files/uploadFile")
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("测试根据URL删除文件")
    void testDeleteByUrl() throws Exception {
        // 准备请求参数
        String fileUrl = "http://example.com/files/test.pdf";

        // 模拟Service行为
        doNothing().when(fileService).deleteByUrl(anyString());

        // 执行请求并验证结果
        mockMvc.perform(delete("/files/deleteByUrl")
                        .param("fileUrl", fileUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(fileService, times(1)).deleteByUrl(anyString());
    }

    @Test
    @DisplayName("测试根据ID删除文件")
    void testDeleteById() throws Exception {
        // 模拟Service行为
        doNothing().when(fileService).deleteById(anyLong());

        // 执行请求并验证结果
        mockMvc.perform(delete("/files/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(fileService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试获取所有文件")
    void testGetAllFiles() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(get("/files/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(fileService, times(1)).getAllFiles();
    }

    @Test
    @DisplayName("测试根据ID查询文件")
    void testSelectById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(get("/files/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(fileService, times(1)).selectById(1L);
    }
} 