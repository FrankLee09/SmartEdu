package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Clazz;
import com.smartedu.mapper.ClazzMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ClazzService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class ClazzServiceTest {

    @Mock
    private ClazzMapper clazzMapper;

    @InjectMocks
    private ClazzService clazzService;

    private Clazz testClazz;
    private List<Clazz> clazzList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testClazz = new Clazz();
        testClazz.setId(1L);
        testClazz.setName("计算机科学1班");
        testClazz.setTeacherId(101L);

        Clazz clazz2 = new Clazz();
        clazz2.setId(2L);
        clazz2.setName("软件工程2班");
        clazz2.setTeacherId(102L);

        clazzList = Arrays.asList(testClazz, clazz2);
    }

    @Test
    @DisplayName("测试获取所有班级")
    void testGetAllClasses() {
        // 模拟ClazzMapper行为
        when(clazzMapper.getAllclasses()).thenReturn(clazzList);

        // 调用被测试方法
        List<Clazz> result = clazzService.getAllclasses();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("计算机科学1班", result.get(0).getName());
        assertEquals("软件工程2班", result.get(1).getName());

        // 验证clazzMapper.getAllclasses方法是否被调用
        verify(clazzMapper, times(1)).getAllclasses();
    }

    @Test
    @DisplayName("测试根据条件查询班级")
    void testSelectAll() {
        // 创建查询条件
        Clazz queryClazz = new Clazz();
        queryClazz.setName("计算机");

        // 模拟ClazzMapper行为
        when(clazzMapper.selectAll(queryClazz)).thenReturn(Arrays.asList(testClazz));

        // 调用被测试方法
        List<Clazz> result = clazzService.selectAll(queryClazz);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("计算机科学1班", result.get(0).getName());

        // 验证clazzMapper.selectAll方法是否被调用
        verify(clazzMapper, times(1)).selectAll(queryClazz);
    }

    @Test
    @DisplayName("测试根据ID查询班级")
    void testSelectById() {
        // 模拟ClazzMapper行为
        when(clazzMapper.selectById(1L)).thenReturn(testClazz);

        // 调用被测试方法
        Clazz result = clazzService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("计算机科学1班", result.getName());

        // 验证clazzMapper.selectById方法是否被调用
        verify(clazzMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询班级")
    void testSelectPage() {
        // 创建查询条件
        Clazz queryClazz = new Clazz();
        
        // 由于PageHelper是静态方法，无法直接模拟，这里只能模拟clazzMapper.selectAll的返回值
        when(clazzMapper.selectAll(queryClazz)).thenReturn(clazzList);

        // 调用被测试方法
        PageInfo<Clazz> result = clazzService.selectPage(queryClazz, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());

        // 验证clazzMapper.selectAll方法是否被调用
        verify(clazzMapper, times(1)).selectAll(queryClazz);
    }

    @Test
    @DisplayName("测试添加班级")
    void testAddClazz() {
        // 调用被测试方法
        clazzService.addClazz(testClazz);

        // 验证clazzMapper.insert方法是否被调用，并且参数是testClazz
        verify(clazzMapper, times(1)).insert(testClazz);
    }

    @Test
    @DisplayName("测试更新班级信息")
    void testUpdateClazz() {
        // 修改班级信息
        testClazz.setName("计算机科学1班(已更新)");

        // 调用被测试方法
        clazzService.updateClazz(testClazz);

        // 验证clazzMapper.update方法是否被调用
        verify(clazzMapper, times(1)).update(testClazz);
    }

    @Test
    @DisplayName("测试删除班级")
    void testDeleteById() {
        // 调用被测试方法
        clazzService.deleteById(1L);

        // 验证clazzMapper.deleteById方法是否被调用
        verify(clazzMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除班级")
    void testDeleteBatch() {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用被测试方法
        clazzService.deleteBatch(ids);

        // 验证clazzMapper.deleteById方法是否被调用两次
        verify(clazzMapper, times(1)).deleteById(1L);
        verify(clazzMapper, times(1)).deleteById(2L);
    }
} 