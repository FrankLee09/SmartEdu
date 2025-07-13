package com.smartedu.service;

import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Task;
import com.smartedu.mapper.TaskMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("测试任务");
        testTask.setContent("这是一个测试任务");
        testTask.setDueDate("2023-07-15");
        testTask.setCourseId(1L);
        testTask.setTag("作业");
        testTask.setTime("2023-07-01");
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("测试任务2");
        task2.setContent("这是第二个测试任务");
        task2.setDueDate("2023-07-20");
        task2.setCourseId(1L);
        task2.setTag("实验");
        task2.setTime("2023-07-05");
        
        taskList = new ArrayList<>();
        taskList.add(testTask);
        taskList.add(task2);
    }

    @Test
    @DisplayName("测试获取所有任务")
    void testGetAllTasks() {
        when(taskMapper.getAllTasks()).thenReturn(taskList);

        List<Task> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("测试任务", result.get(0).getTitle());
        assertEquals("测试任务2", result.get(1).getTitle());
        
        verify(taskMapper, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("测试根据ID查询任务")
    void testSelectById() {
        when(taskMapper.selectById(1L)).thenReturn(testTask);

        Task result = taskService.selectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试任务", result.getTitle());
        
        verify(taskMapper, times(1)).selectById(1L);
    }

    @Test
    @DisplayName("测试根据条件查询任务列表")
    void testSelectAll() {
        Task query = new Task();
        query.setCourseId(1L);
        
        when(taskMapper.selectAll(query)).thenReturn(taskList);

        List<Task> result = taskService.selectAll(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(taskMapper, times(1)).selectAll(query);
    }

    @Test
    @DisplayName("测试添加任务")
    void testAddTask() {
        doNothing().when(taskMapper).insert(testTask);

        taskService.addTask(testTask);
        
        verify(taskMapper, times(1)).insert(testTask);
    }

    @Test
    @DisplayName("测试更新任务")
    void testUpdateTask() {
        doNothing().when(taskMapper).update(testTask);

        testTask.setTitle("更新后的任务标题");
        taskService.updateTask(testTask);
        
        verify(taskMapper, times(1)).update(testTask);
    }

    @Test
    @DisplayName("测试分页查询任务")
    void testSelectPage() {
        Task query = new Task();
        when(taskMapper.selectAll(query)).thenReturn(taskList);

        PageInfo<Task> pageInfo = taskService.selectPage(query, 1, 10);

        assertNotNull(pageInfo);
        assertEquals(2, pageInfo.getList().size());
        
        verify(taskMapper, times(1)).selectAll(query);
    }

    @Test
    @DisplayName("测试添加JSON格式任务")
    void testAddTaskJson() {
        doNothing().when(taskMapper).insertJson(testTask);

        taskService.addTaskJson(testTask);
        
        verify(taskMapper, times(1)).insertJson(testTask);
    }

    @Test
    @DisplayName("测试根据ID删除任务")
    void testDeleteById() {
        doNothing().when(taskMapper).deleteById(1L);

        taskService.deleteById(1L);
        
        verify(taskMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("测试批量删除任务")
    void testDeleteBatch() {
        doNothing().when(taskMapper).deleteById(anyLong());

        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        taskService.deleteBatch(ids);
        
        verify(taskMapper, times(3)).deleteById(anyLong());
        verify(taskMapper, times(1)).deleteById(1L);
        verify(taskMapper, times(1)).deleteById(2L);
        verify(taskMapper, times(1)).deleteById(3L);
    }
} 