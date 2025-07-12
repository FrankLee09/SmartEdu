package com.smartedu.mapper;

import com.smartedu.entity.Course;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseMapper {

    List<Course> getAllCourses();

    List<Course> selectAll(Course course);

    Course selectById(Long id);

    void insert(Course course);

    void update(Course course);

    void deleteById(Long id);
}
