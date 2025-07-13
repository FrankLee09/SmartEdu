package com.smartedu.entity;

import com.smartedu.common.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实体类的测试类，测试getter/setter方法
 */
public class EntityTest {

    @Test
    @DisplayName("测试Student实体类的getter/setter")
    void testStudentGetterSetter() {
        Student student = new Student();
        
        // 测试setter和getter
        student.setId(1L);
        student.setUsername("testuser");
        student.setPassword("password123");
        student.setName("张三");
        student.setEmail("zhangsan@example.com");
        student.setClassId(101L);
        student.setRole("学生");
        
        // 验证getter返回的值是否正确
        assertEquals(1L, student.getId());
        assertEquals("testuser", student.getUsername());
        assertEquals("password123", student.getPassword());
        assertEquals("张三", student.getName());
        assertEquals("zhangsan@example.com", student.getEmail());
        assertEquals(101L, student.getClassId());
        assertEquals("学生", student.getRole());
    }
    
    @Test
    @DisplayName("测试Teacher实体类的getter/setter")
    void testTeacherGetterSetter() {
        Teacher teacher = new Teacher();
        
        // 测试setter和getter
        teacher.setId(1L);
        teacher.setUsername("teacher1");
        teacher.setPassword("password123");
        teacher.setName("李老师");
        teacher.setEmail("teacher@example.com");
        teacher.setRole("教师");
        
        // 验证getter返回的值是否正确
        assertEquals(1L, teacher.getId());
        assertEquals("teacher1", teacher.getUsername());
        assertEquals("password123", teacher.getPassword());
        assertEquals("李老师", teacher.getName());
        assertEquals("teacher@example.com", teacher.getEmail());
        assertEquals("教师", teacher.getRole());
    }
    
    @Test
    @DisplayName("测试Course实体类的getter/setter")
    void testCourseGetterSetter() {
        Course course = new Course();
        
        // 测试setter和getter
        course.setId(1L);
        course.setTitle("Java编程");
        course.setDescription("Java编程基础课程");
        course.setTeacherId(101L);
        course.setTeacherName("王教授");
        course.setTime("2023-07-01 10:00:00");
        
        // 验证getter返回的值是否正确
        assertEquals(1L, course.getId());
        assertEquals("Java编程", course.getTitle());
        assertEquals("Java编程基础课程", course.getDescription());
        assertEquals(101L, course.getTeacherId());
        assertEquals("王教授", course.getTeacherName());
        assertEquals("2023-07-01 10:00:00", course.getTime());
    }
    
    @Test
    @DisplayName("测试Account实体类的getter/setter")
    void testAccountGetterSetter() {
        Account account = new Account();
        
        // 测试setter和getter
        account.setId(1L);
        account.setUsername("user1");
        account.setPassword("password123");
        account.setNewPassword("newpassword123");
        account.setRole("学生");
        
        // 验证getter返回的值是否正确
        assertEquals(1L, account.getId());
        assertEquals("user1", account.getUsername());
        assertEquals("password123", account.getPassword());
        assertEquals("newpassword123", account.getNewPassword());
        assertEquals("学生", account.getRole());
    }
    
    @Test
    @DisplayName("测试File实体类的getter/setter")
    void testFileGetterSetter() {
        File file = new File();
        
        // 测试setter和getter
        file.setId(1L);
        file.setFilename("test.pdf");
        file.setFileUrl("/uploads/test.pdf");
        file.setDescription("测试文件");
        file.setTag("文档");
        file.setTaskId(1L);
        file.setCourseId(2L);
        file.setStudentId(3L);
        file.setSubmissionId(4L);
        file.setFinished(100.0);
        
        // 验证getter返回的值是否正确
        assertEquals(1L, file.getId());
        assertEquals("test.pdf", file.getFilename());
        assertEquals("/uploads/test.pdf", file.getFileUrl());
        assertEquals("测试文件", file.getDescription());
        assertEquals("文档", file.getTag());
        assertEquals(1L, file.getTaskId());
        assertEquals(2L, file.getCourseId());
        assertEquals(3L, file.getStudentId());
        assertEquals(4L, file.getSubmissionId());
        assertEquals(100.0, file.getFinished());
    }
    
    @Test
    @DisplayName("测试Result类的静态方法")
    void testResultStaticMethods() {
        // 测试success()方法
        Result result1 = Result.success();
        assertEquals("200", result1.getCode());
        assertEquals("请求成功", result1.getMsg());
        assertNull(result1.getData());
        
        // 测试success(Object data)方法
        String testData = "测试数据";
        Result result2 = Result.success(testData);
        assertEquals("200", result2.getCode());
        assertEquals("请求成功", result2.getMsg());
        assertEquals(testData, result2.getData());
        
        // 测试error()方法
        Result result3 = Result.error();
        assertEquals("500", result3.getCode());
        assertEquals("系统错误", result3.getMsg());
        assertNull(result3.getData());
        
        // 测试error(String code, String msg)方法
        Result result4 = Result.error("404", "资源不存在");
        assertEquals("404", result4.getCode());
        assertEquals("资源不存在", result4.getMsg());
        assertNull(result4.getData());
    }
} 