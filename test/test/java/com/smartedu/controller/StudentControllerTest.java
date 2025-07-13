package com.smartedu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.smartedu.config.TestConfig;
import com.smartedu.entity.Student;
import com.smartedu.mapper.ClazzMapper;
import com.smartedu.mapper.CourseMapper;
import com.smartedu.mapper.StudentMapper;
import com.smartedu.mapper.TeacherMapper;
import com.smartedu.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(TestConfig.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;
    
    @MockBean
    private StudentMapper studentMapper;
    
    @MockBean
    private TeacherMapper teacherMapper;
    
    @MockBean
    private CourseMapper courseMapper;
    
    @MockBean
    private ClazzMapper clazzMapper;

    private Student student1;
    private Student student2;
    private List<Student> studentList;
    private PageInfo<Student> pageInfo;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        student1 = new Student();
        student1.setId(1L);
        student1.setUsername("student1");
        student1.setPassword("password123");
        student1.setName("学生1");
        student1.setEmail("student1@example.com");
        student1.setClassId(101L);

        student2 = new Student();
        student2.setId(2L);
        student2.setUsername("student2");
        student2.setPassword("password456");
        student2.setName("学生2");
        student2.setEmail("student2@example.com");
        student2.setClassId(102L);

        studentList = Arrays.asList(student1, student2);
        
        // 创建分页信息
        pageInfo = new PageInfo<>(studentList);
    }

    @Test
    @DisplayName("测试获取所有学生")
    void testGetAllStudents() throws Exception {
        // 模拟Service行为
        when(studentService.getALlstudents()).thenReturn(studentList);

        // 执行请求并验证结果
        mockMvc.perform(get("/student/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].username").value("student1"))
                .andExpect(jsonPath("$.data[1].username").value("student2"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).getALlstudents();
    }

    @Test
    @DisplayName("测试根据条件查询学生")
    void testSelectAll() throws Exception {
        // 创建查询条件
        Student queryStudent = new Student();
        queryStudent.setClassId(101L);

        // 模拟Service行为
        when(studentService.selectAll(any(Student.class))).thenReturn(Arrays.asList(student1));

        // 执行请求并验证结果
        mockMvc.perform(post("/student/selectAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].username").value("student1"))
                .andExpect(jsonPath("$.data[0].classId").value(101));

        // 验证service方法是否被调用
        verify(studentService, times(1)).selectAll(any(Student.class));
    }

    @Test
    @DisplayName("测试根据ID查询学生")
    void testSelectById() throws Exception {
        // 模拟Service行为
        when(studentService.selectById(1L)).thenReturn(student1);

        // 执行请求并验证结果
        mockMvc.perform(get("/student/selectById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.username").value("student1"))
                .andExpect(jsonPath("$.data.name").value("学生1"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询学生")
    void testSelectPage() throws Exception {
        // 模拟Service行为
        when(studentService.selectPage(any(Student.class), eq(1), eq(10))).thenReturn(pageInfo);

        // 执行请求并验证结果
        mockMvc.perform(get("/student/selectPage")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.list[0].username").value("student1"))
                .andExpect(jsonPath("$.data.list[1].username").value("student2"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).selectPage(any(Student.class), eq(1), eq(10));
    }

    @Test
    @DisplayName("测试添加学生")
    void testAdd() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(post("/student/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).addStudent(any(Student.class));
    }

    @Test
    @DisplayName("测试删除学生")
    void testDeleteById() throws Exception {
        // 执行请求并验证结果
        mockMvc.perform(delete("/student/deleteById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除学生")
    void testDeleteBatch() throws Exception {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 执行请求并验证结果
        mockMvc.perform(delete("/student/deleteBatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).deleteBatch(anyList());
    }

    @Test
    @DisplayName("测试更新学生信息")
    void testUpdate() throws Exception {
        // 修改学生信息
        student1.setName("学生1(已更新)");
        student1.setEmail("updated@example.com");

        // 执行请求并验证结果
        mockMvc.perform(put("/student/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        // 验证service方法是否被调用
        verify(studentService, times(1)).updateStudent(any(Student.class));
    }
} 