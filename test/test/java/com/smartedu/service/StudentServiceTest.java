package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Account;
import com.smartedu.entity.Student;
import com.smartedu.exception.CustomException;
import com.smartedu.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * StudentService的单元测试类
 */
@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("张三");
        testStudent.setEmail("zhangsan@example.com");
        testStudent.setUsername("zhangsan");
        testStudent.setPassword("password123");
        testStudent.setClassId(101L);

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("李四");
        student2.setEmail("lisi@example.com");
        student2.setUsername("lisi");
        student2.setPassword("password456");
        student2.setClassId(102L);

        studentList = Arrays.asList(testStudent, student2);
    }

    @Test
    @DisplayName("测试获取所有学生")
    void testGetAllStudents() {
        // 模拟StudentMapper行为
        when(studentMapper.getAllstudents()).thenReturn(studentList);

        // 调用被测试方法
        List<Student> result = studentService.getALlstudents();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
        assertEquals("李四", result.get(1).getName());

        // 验证studentMapper.getAllstudents方法是否被调用
        verify(studentMapper, times(1)).getAllstudents();
    }

    @Test
    @DisplayName("测试根据条件查询学生")
    void testSelectAll() {
        // 创建查询条件
        Student queryStudent = new Student();
        queryStudent.setClassId(101L);

        // 模拟StudentMapper行为
        when(studentMapper.selectAll(queryStudent)).thenReturn(Arrays.asList(testStudent));

        // 调用被测试方法
        List<Student> result = studentService.selectAll(queryStudent);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getName());

        // 验证studentMapper.selectAll方法是否被调用
        verify(studentMapper, times(1)).selectAll(queryStudent);
    }

    @Test
    @DisplayName("测试根据ID查询学生")
    void testSelectById() {
        // 模拟StudentMapper行为
        when(studentMapper.selectById(1L)).thenReturn(testStudent);

        // 调用被测试方法
        Student result = studentService.selectById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("张三", result.getName());

        // 验证studentMapper.selectById方法是否被调用
        verify(studentMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试分页查询学生")
    void testSelectPage() {
        // 创建查询条件
        Student queryStudent = new Student();
        
        // 由于PageHelper是静态方法，无法直接模拟，这里只能模拟studentMapper.selectAll的返回值
        when(studentMapper.selectAll(queryStudent)).thenReturn(studentList);

        // 调用被测试方法
        PageInfo<Student> result = studentService.selectPage(queryStudent, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());

        // 验证studentMapper.selectAll方法是否被调用
        verify(studentMapper, times(1)).selectAll(queryStudent);
    }

    @Test
    @DisplayName("测试添加学生")
    void testAddStudent() {
        // 调用被测试方法
        studentService.addStudent(testStudent);

        // 验证studentMapper.insert方法是否被调用，并且参数是testStudent
        verify(studentMapper, times(1)).insert(testStudent);
    }

    @Test
    @DisplayName("测试更新学生信息")
    void testUpdateStudent() {
        // 修改学生信息
        testStudent.setName("张三(已更新)");
        testStudent.setEmail("zhangsan_updated@example.com");

        // 调用被测试方法
        studentService.updateStudent(testStudent);

        // 验证studentMapper.update方法是否被调用
        verify(studentMapper, times(1)).update(testStudent);
    }

    @Test
    @DisplayName("测试删除学生")
    void testDeleteById() {
        // 调用被测试方法
        studentService.deleteById(1L);

        // 验证studentMapper.deleteById方法是否被调用
        verify(studentMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除学生")
    void testDeleteBatch() {
        // 准备要删除的ID列表
        List<Long> ids = Arrays.asList(1L, 2L);

        // 调用被测试方法
        studentService.deleteBatch(ids);

        // 验证studentMapper.deleteById方法是否被调用两次
        verify(studentMapper, times(1)).deleteById(1L);
        verify(studentMapper, times(1)).deleteById(2L);
    }

    @Test
    @DisplayName("测试学生登录 - 成功")
    void testLogin_Success() {
        // 准备登录账号
        Account account = new Account();
        account.setUsername("zhangsan");
        account.setPassword("password123");

        // 模拟StudentMapper行为
        when(studentMapper.selectByUsername("zhangsan")).thenReturn(testStudent);

        // 调用被测试方法
        Student result = studentService.login(account);

        // 验证结果
        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());

        // 验证studentMapper.selectByUsername方法是否被调用
        verify(studentMapper, times(1)).selectByUsername("zhangsan");
    }

    @Test
    @DisplayName("测试学生登录 - 账号不存在")
    void testLogin_AccountNotExist() {
        // 准备登录账号
        Account account = new Account();
        account.setUsername("nonexistent");
        account.setPassword("password123");

        // 模拟StudentMapper行为，返回null表示账号不存在
        when(studentMapper.selectByUsername("nonexistent")).thenReturn(null);

        // 调用被测试方法并验证是否抛出异常
        CustomException exception = assertThrows(CustomException.class, () -> {
            studentService.login(account);
        });

        // 验证异常信息
        assertEquals("500", exception.getCode());
        assertEquals("该账号不存在", exception.getMessage());

        // 验证studentMapper.selectByUsername方法是否被调用
        verify(studentMapper, times(1)).selectByUsername("nonexistent");
    }

    @Test
    @DisplayName("测试学生登录 - 密码错误")
    void testLogin_WrongPassword() {
        // 准备登录账号
        Account account = new Account();
        account.setUsername("zhangsan");
        account.setPassword("wrongpassword");

        // 模拟StudentMapper行为
        when(studentMapper.selectByUsername("zhangsan")).thenReturn(testStudent);

        // 调用被测试方法并验证是否抛出异常
        CustomException exception = assertThrows(CustomException.class, () -> {
            studentService.login(account);
        });

        // 验证异常信息
        assertEquals("500", exception.getCode());
        assertEquals("账号或密码错误", exception.getMessage());

        // 验证studentMapper.selectByUsername方法是否被调用
        verify(studentMapper, times(1)).selectByUsername("zhangsan");
    }

    @Test
    @DisplayName("测试学生注册")
    void testRegister() {
        // 准备新学生
        Student newStudent = new Student();
        newStudent.setUsername("newstudent");
        newStudent.setPassword("newpassword");
        newStudent.setName("新学生");
        newStudent.setEmail("newstudent@example.com");

        // 模拟StudentMapper行为
        when(studentMapper.selectByUsername("newstudent")).thenReturn(newStudent);

        // 调用被测试方法
        Student result = studentService.register(newStudent);

        // 验证结果
        assertNotNull(result);
        assertEquals("newstudent", result.getUsername());

        // 验证studentMapper方法是否被调用
        verify(studentMapper, times(1)).insert(newStudent);
        verify(studentMapper, times(1)).selectByUsername("newstudent");
    }

    @Test
    @DisplayName("测试更新密码 - 成功")
    void testUpdatePassword_Success() {
        // 准备账号信息
        Account account = new Account();
        account.setId(1L);
        account.setPassword("password123");
        account.setNewPassword("newpassword123");

        // 模拟StudentMapper行为
        when(studentMapper.selectById(1L)).thenReturn(testStudent);

        // 调用被测试方法
        studentService.updatePassword(account);

        // 验证studentMapper方法是否被调用
        verify(studentMapper, times(1)).selectById(1L);
        verify(studentMapper, times(1)).update(any(Student.class));
    }

    @Test
    @DisplayName("测试更新密码 - 原密码错误")
    void testUpdatePassword_WrongOldPassword() {
        // 准备账号信息
        Account account = new Account();
        account.setId(1L);
        account.setPassword("wrongpassword");
        account.setNewPassword("newpassword123");

        // 模拟StudentMapper行为
        when(studentMapper.selectById(1L)).thenReturn(testStudent);

        // 调用被测试方法并验证是否抛出异常
        CustomException exception = assertThrows(CustomException.class, () -> {
            studentService.updatePassword(account);
        });

        // 验证异常信息
        assertEquals("500", exception.getCode());
        assertEquals("原密码输入错误，请输入正确密码", exception.getMessage());

        // 验证studentMapper.selectById方法是否被调用
        verify(studentMapper, times(1)).selectById(1L);
        // 验证studentMapper.update方法没有被调用
        verify(studentMapper, never()).update(any(Student.class));
    }
} 