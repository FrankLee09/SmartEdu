package com.smartedu.mapper;

import com.smartedu.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.awt.image.VolatileImage;
import java.util.List;

@Mapper
public interface TaskMapper {

     List<Task> getAllTasks();
     Task selectById(Long id);
     List<Task> selectAll(Task task);
     void insert(Task task);
     void insertJson(Task task);
     void update(Task task);
     void deleteById(Long id);

}
