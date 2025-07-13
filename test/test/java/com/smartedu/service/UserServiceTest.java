package com.smartedu.service;

import com.smartedu.entity.User;
import com.smartedu.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService的单元测试类
 * 演示如何使用JUnit 5和Mockito进行单元测试
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper; // 模拟依赖

    @InjectMocks
    private UserService userService; // 被测试的服务类

    private User testUser;

    @BeforeEach
    void setUp() {
        // 在每个测试方法执行前准备测试数据
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("测试根据ID查询用户 - 正常情况")
    void testGetUserById_Success() {
        // 模拟UserMapper的行为
        when(userMapper.selectById(1)).thenReturn(testUser);

        // 调用被测试方法
        User result = userService.getUserById(1);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals("test@example.com", result.getEmail());

        // 验证userMapper.selectById方法是否被调用了一次
        verify(userMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("测试根据ID查询用户 - 用户不存在")
    void testGetUserById_NotFound() {
        // 模拟UserMapper的行为，返回null表示用户不存在
        when(userMapper.selectById(999)).thenReturn(null);

        // 调用被测试方法
        User result = userService.getUserById(999);

        // 验证结果
        assertNull(result);

        // 验证userMapper.selectById方法是否被调用了一次
        verify(userMapper, times(1)).selectById(999);
    }

    @Test
    @DisplayName("测试添加用户 - 正常情况")
    void testAddUser_Success() {
        // 模拟UserMapper的行为
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 调用被测试方法
        boolean result = userService.addUser(testUser);

        // 验证结果
        assertTrue(result);

        // 验证userMapper.insert方法是否被调用了一次，并且参数是testUser
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    @DisplayName("测试添加用户 - 失败情况")
    void testAddUser_Failure() {
        // 模拟UserMapper的行为，返回0表示插入失败
        when(userMapper.insert(any(User.class))).thenReturn(0);

        // 调用被测试方法
        boolean result = userService.addUser(testUser);

        // 验证结果
        assertFalse(result);

        // 验证userMapper.insert方法是否被调用了一次
        verify(userMapper, times(1)).insert(testUser);
    }

    @Test
    @DisplayName("测试更新用户信息 - 正常情况")
    void testUpdateUser_Success() {
        // 模拟UserMapper的行为
        when(userMapper.update(any(User.class))).thenReturn(1);

        // 修改测试用户的信息
        testUser.setUsername("updateduser");
        testUser.setEmail("updated@example.com");

        // 调用被测试方法
        boolean result = userService.updateUser(testUser);

        // 验证结果
        assertTrue(result);

        // 验证userMapper.update方法是否被调用了一次
        verify(userMapper, times(1)).update(testUser);
    }

    @Test
    @DisplayName("测试删除用户 - 正常情况")
    void testDeleteUser_Success() {

        when(userMapper.deleteById(1)).thenReturn(1);

        // 调用被测试方法
        boolean result = userService.deleteUser(1);

        // 验证结果
        assertTrue(result);

        // 验证userMapper.deleteById方法是否被调用了一次
        verify(userMapper, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("测试用户登录 - 用户名密码正确")
    void testLogin_Success() {
        // 模拟UserMapper的行为
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // 调用被测试方法
        User result = userService.login("testuser", "password123");

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        // 验证userMapper.selectByUsername方法是否被调用了一次
        verify(userMapper, times(1)).selectByUsername("testuser");
    }

    @Test
    @DisplayName("测试用户登录 - 密码错误")
    void testLogin_WrongPassword() {
        // 模拟UserMapper的行为
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // 调用被测试方法并验证是否抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });

        // 验证异常消息
        assertEquals("密码错误", exception.getMessage());

        // 验证userMapper.selectByUsername方法是否被调用了一次
        verify(userMapper, times(1)).selectByUsername("testuser");
    }

    @Test
    @DisplayName("测试用户登录 - 用户不存在")
    void testLogin_UserNotFound() {
        // 模拟UserMapper的行为，返回null表示用户不存在
        when(userMapper.selectByUsername("nonexistentuser")).thenReturn(null);

        // 调用被测试方法并验证是否抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login("nonexistentuser", "password123");
        });

        // 验证异常消息
        assertEquals("用户不存在", exception.getMessage());

        // 验证userMapper.selectByUsername方法是否被调用了一次
        verify(userMapper, times(1)).selectByUsername("nonexistentuser");
    }
} 