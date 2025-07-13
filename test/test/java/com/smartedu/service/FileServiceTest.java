package com.smartedu.service;

import com.smartedu.entity.File;
import com.smartedu.mapper.FileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * FileService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileService fileService;

    private File testFile;
    private List<File> fileList;
    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testFile = new File();
        testFile.setId(1L);
        testFile.setFilename("test.pdf");
        testFile.setFileUrl("/uploads/test.pdf");
        testFile.setDescription("测试文件");
        testFile.setTag("文档");

        File file2 = new File();
        file2.setId(2L);
        file2.setFilename("example.jpg");
        file2.setFileUrl("/uploads/example.jpg");
        file2.setDescription("示例图片");
        file2.setTag("图片");

        fileList = Arrays.asList(testFile, file2);

        // 创建模拟的MultipartFile
        mockMultipartFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "测试文件内容".getBytes()
        );
    }

    @Test
    @DisplayName("测试获取所有文件")
    void testSelectAll() {
        // 创建查询条件
        File queryFile = new File();

        // 模拟FileMapper行为
        when(fileMapper.selectAll(queryFile)).thenReturn(fileList);

        // 调用被测试方法
        List<File> result = fileService.selectAll(queryFile);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test.pdf", result.get(0).getFilename());
        assertEquals("example.jpg", result.get(1).getFilename());

        // 验证fileMapper.selectAll方法是否被调用
        verify(fileMapper, times(1)).selectAll(queryFile);
    }

    @Test
    @DisplayName("测试根据ID查询文件")
    void testSelectById() {
        // 模拟FileMapper行为
        when(fileMapper.selectById(1L)).thenReturn(testFile);

        // 调用被测试方法
        File result = fileService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test.pdf", result.getFilename());

        // 验证fileMapper.selectById方法是否被调用
        verify(fileMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加文件")
    void testAddFile() {
        // 模拟FileMapper行为
        doNothing().when(fileMapper).insert(any(File.class));

        // 调用被测试方法
        fileService.addFile(testFile);

        // 验证fileMapper.insert方法是否被调用
        verify(fileMapper, times(1)).insert(testFile);
    }

    @Test
    @DisplayName("测试更新文件信息")
    void testUpdateFile() {
        // 修改文件信息
        testFile.setFilename("updated_test.pdf");
        testFile.setDescription("更新后的测试文件");

        // 模拟FileMapper行为
        when(fileMapper.selectById(1L)).thenReturn(testFile);
        doNothing().when(fileMapper).update(testFile);

        // 调用被测试方法
        fileService.updateFile(testFile);

        // 验证fileMapper.update方法是否被调用
        verify(fileMapper, times(1)).update(testFile);
    }

    @Test
    @DisplayName("测试删除文件")
    void testDeleteById() {
        // 模拟FileMapper行为
        when(fileMapper.selectById(1L)).thenReturn(testFile);
        doNothing().when(fileMapper).deleteById(1L);

        // 调用被测试方法
        fileService.deleteById(1L);

        // 验证fileMapper.deleteById方法是否被调用
        verify(fileMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试文件上传功能")
    void testFileUpload() throws IOException {
        // 由于实际的文件上传涉及到文件系统操作，这里我们只能测试部分逻辑
        // 实际项目中，可能需要重构FileService，使其更易于测试
        
        // 这个测试用例仅作为示例，实际项目中可能需要更复杂的测试逻辑
        File uploadedFile = new File();
        uploadedFile.setFilename("test.pdf");
        uploadedFile.setFileUrl("/uploads/test.pdf");
        
        // 这里我们不测试实际的文件上传，因为FileService中没有直接的uploadFile方法
        // 实际项目中应该有一个Controller层调用FileService的方法处理文件上传
    }
} 