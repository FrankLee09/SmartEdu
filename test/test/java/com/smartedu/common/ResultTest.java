package com.smartedu.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result的单元测试类
 */
public class ResultTest {

    @Test
    @DisplayName("测试Result的success()方法")
    void testSuccessWithoutData() {
        Result result = Result.success();
        
        assertEquals("200", result.getCode());
        assertEquals("请求成功", result.getMsg());
        assertNull(result.getData());
    }
    
    @Test
    @DisplayName("测试Result的success(Object data)方法")
    void testSuccessWithData() {
        String testData = "测试数据";
        Result result = Result.success(testData);
        
        assertEquals("200", result.getCode());
        assertEquals("请求成功", result.getMsg());
        assertEquals(testData, result.getData());
    }
    
    @Test
    @DisplayName("测试Result的error()方法")
    void testErrorWithoutParams() {
        Result result = Result.error();
        
        assertEquals("500", result.getCode());
        assertEquals("系统错误", result.getMsg());
        assertNull(result.getData());
    }
    
    @Test
    @DisplayName("测试Result的error(String code, String msg)方法")
    void testErrorWithParams() {
        Result result = Result.error("404", "资源不存在");
        
        assertEquals("404", result.getCode());
        assertEquals("资源不存在", result.getMsg());
        assertNull(result.getData());
    }
    
    @Test
    @DisplayName("测试Result的setter和getter方法")
    void testSetterAndGetter() {
        Result result = new Result();
        
        result.setCode("200");
        result.setMsg("操作成功");
        result.setData("测试数据");
        
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals("测试数据", result.getData());
    }
} 