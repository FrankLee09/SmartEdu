package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.User;
import com.smartedu.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@Import(TestConfig.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private Map<String, Object> loginParams;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setRole("admin");

        loginParams = new HashMap<>();
        loginParams.put("username", "testuser");
        loginParams.put("password", "password123");
    }

    @Test
    @DisplayName("测试登录成功")
    void testLoginSuccess() throws Exception {
        // 模拟Service行为
        when(userService.login(any(User.class))).thenReturn(testUser);

        // 执行请求并验证结果
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("admin"));

        // 验证service方法是否被调用
        verify(userService, times(1)).login(any(User.class));
    }

    @Test
    @DisplayName("测试登录失败 - 用户不存在")
    void testLoginFailUserNotExist() throws Exception {
        // 模拟Service行为
        when(userService.login(any(User.class))).thenReturn(null);

        // 执行请求并验证结果
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));

        // 验证service方法是否被调用
        verify(userService, times(1)).login(any(User.class));
    }

    @Test
    @DisplayName("测试注册")
    void testRegister() throws Exception {
        // 模拟Service行为
        when(userService.register(any(User.class))).thenReturn(true);

        // 执行请求并验证结果
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("注册成功"));

        // 验证service方法是否被调用
        verify(userService, times(1)).register(any(User.class));
    }

    @Test
    @DisplayName("测试注册失败 - 用户已存在")
    void testRegisterFailUserExists() throws Exception {
        // 模拟Service行为
        when(userService.register(any(User.class))).thenReturn(false);

        // 执行请求并验证结果
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("用户名已存在"));

        // 验证service方法是否被调用
        verify(userService, times(1)).register(any(User.class));
    }
} 