package com.smartedu.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomException的单元测试类
 */
public class CustomExceptionTest {

    @Test
    @DisplayName("测试CustomException构造函数和getter方法")
    void testCustomExceptionConstructorAndGetters() {
        // 创建CustomException实例
        String code = "404";
        String message = "资源不存在";
        CustomException exception = new CustomException(code, message);
        
        // 验证getter方法返回的值是否正确
        assertEquals(code, exception.getCode());
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    @DisplayName("测试CustomException的继承关系")
    void testCustomExceptionInheritance() {
        // 验证CustomException是否继承自RuntimeException
        CustomException exception = new CustomException("500", "系统错误");
        assertTrue(exception instanceof RuntimeException);
    }
    
    @Test
    @DisplayName("测试抛出CustomException")
    void testThrowCustomException() {
        // 验证是否能正确抛出并捕获CustomException
        CustomException exception = assertThrows(CustomException.class, () -> {
            throw new CustomException("403", "权限不足");
        });
        
        assertEquals("403", exception.getCode());
        assertEquals("权限不足", exception.getMessage());
    }
} 