package com.smartedu.service;

import com.smartedu.entity.Account;
import com.smartedu.entity.Teacher;
import com.smartedu.exception.CustomException;
import com.smartedu.mapper.TeacherMapper;
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
 * TeacherService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;
    private List<Teacher> teacherList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setName("张教授");
        testTeacher.setEmail("zhang@example.com");
        testTeacher.setUsername("zhang");
        testTeacher.setPassword("password123");
        testTeacher.setRole("teacher");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setName("李教授");
        teacher2.setEmail("li@example.com");
        teacher2.setUsername("li");
        teacher2.setPassword("password456");
        teacher2.setRole("teacher");

        teacherList = Arrays.asList(testTeacher, teacher2);
    }

    @Test
    @DisplayName("测试获取所有教师")
    void testGetAllTeachers() {
        // 模拟TeacherMapper行为
        when(teacherMapper.getAllTeachers()).thenReturn(teacherList);

        // 调用被测试方法
        List<Teacher> result = teacherService.getAllTeachers();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张教授", result.get(0).getName());
        assertEquals("李教授", result.get(1).getName());

        // 验证teacherMapper.getAllTeachers方法是否被调用
        verify(teacherMapper, times(1)).getAllTeachers();
    }

    @Test
    @DisplayName("测试根据条件查询教师")
    void testSelectAll() {
        // 创建查询条件
        Teacher queryTeacher = new Teacher();
        queryTeacher.setName("张");

        // 模拟TeacherMapper行为
        when(teacherMapper.selectAll(queryTeacher)).thenReturn(Arrays.asList(testTeacher));

        // 调用被测试方法
        List<Teacher> result = teacherService.selectAll(queryTeacher);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张教授", result.get(0).getName());

        // 验证teacherMapper.selectAll方法是否被调用
        verify(teacherMapper, times(1)).selectAll(queryTeacher);
    }

    @Test
    @DisplayName("测试根据ID查询教师")
    void testSelectById() {
        // 模拟TeacherMapper行为
        when(teacherMapper.selectById(1L)).thenReturn(testTeacher);

        // 调用被测试方法
        Teacher result = teacherService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张教授", result.getName());

        // 验证teacherMapper.selectById方法是否被调用
        verify(teacherMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试添加教师")
    void testInsert() {
        // 调用被测试方法
        teacherService.insert(testTeacher);

        // 验证teacherMapper.insert方法是否被调用，并且参数是testTeacher
        verify(teacherMapper, times(1)).insert(testTeacher);
    }

    @Test
    @DisplayName("测试更新教师信息")
    void testUpdate() {
        // 修改教师信息
        testTeacher.setName("张教授(已更新)");
        testTeacher.setEmail("zhang_updated@example.com");

        // 调用被测试方法
        teacherService.update(testTeacher);

        // 验证teacherMapper.update方法是否被调用
        verify(teacherMapper, times(1)).update(testTeacher);
    }

    @Test
    @DisplayName("测试删除教师")
    void testDeleteById() {
        // 调用被测试方法
        teacherService.deleteById(1L);

        // 验证teacherMapper.deleteById方法是否被调用
        verify(teacherMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试教师登录成功")
    void testLoginSuccess() {
        // 准备测试数据
        Account account = new Account();
        account.setUsername("zhang");
        account.setPassword("password123");

        // 模拟TeacherMapper行为
        when(teacherMapper.selectByUsername("zhang")).thenReturn(testTeacher);

        // 调用被测试方法
        Teacher result = teacherService.login(account);

        // 验证结果
        assertNotNull(result);
        assertEquals("zhang", result.getUsername());
        assertEquals("张教授", result.getName());

        // 验证teacherMapper.selectByUsername方法是否被调用
        verify(teacherMapper, times(1)).selectByUsername("zhang");
    }

    @Test
    @DisplayName("测试教师登录失败 - 账号不存在")
    void testLoginFailAccountNotExist() {
        // 准备测试数据
        Account account = new Account();
        account.setUsername("notexist");
        account.setPassword("password123");

        // 模拟TeacherMapper行为
        when(teacherMapper.selectByUsername("notexist")).thenReturn(null);

        // 验证抛出的异常
        CustomException exception = assertThrows(CustomException.class, () -> {
            teacherService.login(account);
        });
        assertEquals("500", exception.getCode());
        assertEquals("该账号不存在", exception.getMessage());

        // 验证teacherMapper.selectByUsername方法是否被调用
        verify(teacherMapper, times(1)).selectByUsername("notexist");
    }

    @Test
    @DisplayName("测试教师登录失败 - 密码错误")
    void testLoginFailWrongPassword() {
        // 准备测试数据
        Account account = new Account();
        account.setUsername("zhang");
        account.setPassword("wrongpassword");

        // 模拟TeacherMapper行为
        when(teacherMapper.selectByUsername("zhang")).thenReturn(testTeacher);

        // 验证抛出的异常
        CustomException exception = assertThrows(CustomException.class, () -> {
            teacherService.login(account);
        });
        assertEquals("500", exception.getCode());
        assertEquals("账号或密码错误", exception.getMessage());

        // 验证teacherMapper.selectByUsername方法是否被调用
        verify(teacherMapper, times(1)).selectByUsername("zhang");
    }

    @Test
    @DisplayName("测试教师注册")
    void testRegister() {
        // 准备测试数据
        Teacher newTeacher = new Teacher();
        newTeacher.setUsername("newteacher");
        newTeacher.setPassword("password789");
        newTeacher.setName("王教授");
        newTeacher.setEmail("wang@example.com");

        // 模拟TeacherMapper行为
        when(teacherMapper.selectByUsername("newteacher")).thenReturn(newTeacher);

        // 调用被测试方法
        Teacher result = teacherService.register(newTeacher);

        // 验证结果
        assertNotNull(result);
        assertEquals("newteacher", result.getUsername());
        assertEquals("王教授", result.getName());

        // 验证teacherMapper方法是否被调用
        verify(teacherMapper, times(1)).insert(newTeacher);
        verify(teacherMapper, times(1)).selectByUsername("newteacher");
    }
} 