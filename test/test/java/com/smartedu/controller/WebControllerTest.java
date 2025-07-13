package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.entity.Teacher;
import com.smartedu.exception.CustomException;
import com.smartedu.mapper.ClazzMapper;
import com.smartedu.mapper.CourseMapper;
import com.smartedu.mapper.StudentMapper;
import com.smartedu.mapper.TeacherMapper;
import com.smartedu.service.StudentService;
import com.smartedu.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
@Import(TestConfig.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private TeacherService teacherService;
    
    @MockBean
    private StudentMapper studentMapper;
    
    @MockBean
    private TeacherMapper teacherMapper;
    
    @MockBean
    private CourseMapper courseMapper;
    
    @MockBean
    private ClazzMapper clazzMapper;

    private Account studentAccount;
    private Account teacherAccount;
    private Student student;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        studentAccount = new Account();
        studentAccount.setId(1L);
        studentAccount.setUsername("student1");
        studentAccount.setPassword("password123");
        studentAccount.setRole("学生");

        teacherAccount = new Account();
        teacherAccount.setId(2L);
        teacherAccount.setUsername("teacher1");
        teacherAccount.setPassword("password456");
        teacherAccount.setRole("教师");

        student = new Student();
        student.setId(1L);
        student.setUsername("student1");
        student.setPassword("password123");
        student.setName("学生1");
        student.setEmail("student1@example.com");

        teacher = new Teacher();
        teacher.setId(2L);
        teacher.setUsername("teacher1");
        teacher.setPassword("password456");
        teacher.setName("教师1");
        teacher.setEmail("teacher1@example.com");
        teacher.setRole("教师");
    }

    @Test
    @DisplayName("测试学生登录成功")
    void testStudentLoginSuccess() throws Exception {
        // 模拟Service行为
        when(studentService.login(any(Account.class))).thenReturn(student);

        // 执行请求并验证结果
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("student1"))
                .andExpect(jsonPath("$.data.name").value("学生1"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).login(any(Account.class));
    }

    @Test
    @DisplayName("测试教师登录成功")
    void testTeacherLoginSuccess() throws Exception {
        // 模拟Service行为
        when(teacherService.login(any(Account.class))).thenReturn(teacher);

        // 执行请求并验证结果
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("teacher1"))
                .andExpect(jsonPath("$.data.name").value("教师1"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).login(any(Account.class));
    }

    @Test
    @DisplayName("测试登录失败 - 非法角色")
    void testLoginFailInvalidRole() throws Exception {
        // 准备非法角色的账号
        Account invalidAccount = new Account();
        invalidAccount.setUsername("invalid");
        invalidAccount.setPassword("password");
        invalidAccount.setRole("非法角色");

        // 模拟异常抛出
        doThrow(new CustomException("500", "非法输入")).when(studentService).login(any(Account.class));

        // 执行请求并验证结果
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("非法输入"));
    }

    @Test
    @DisplayName("测试学生注册成功")
    void testStudentRegisterSuccess() throws Exception {
        // 模拟Service行为
        when(studentService.register(any(Student.class))).thenReturn(student);

        // 执行请求并验证结果
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("student1"))
                .andExpect(jsonPath("$.data.name").value("学生1"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).register(any(Student.class));
    }

    @Test
    @DisplayName("测试教师注册成功")
    void testTeacherRegisterSuccess() throws Exception {
        // 模拟Service行为
        when(teacherService.register(any(Teacher.class))).thenReturn(teacher);

        // 执行请求并验证结果
        mockMvc.perform(post("/teacherRegister")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("teacher1"))
                .andExpect(jsonPath("$.data.name").value("教师1"))
                .andExpect(jsonPath("$.data.role").value("教师"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).register(any(Teacher.class));
    }

    @Test
    @DisplayName("测试学生更新密码成功")
    void testStudentUpdatePasswordSuccess() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(put("/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).updatePassword(any(Account.class));
    }

    @Test
    @DisplayName("测试教师更新密码成功")
    void testTeacherUpdatePasswordSuccess() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(put("/updatePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(teacherService, times(1)).updatePassword(any(Account.class));
    }
} 