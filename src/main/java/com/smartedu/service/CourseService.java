package com.smartedu.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smartedu.entity.Course;
import com.smartedu.entity.Student;
import com.smartedu.mapper.CourseMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Resource
    CourseMapper courseMapper;

    public List<Course> getAllCourses() {
        return courseMapper.getAllCourses();
    }

    public List<Course> selectAll(Course course) {
        return courseMapper.selectAll(course);
    }

    public Course selectById(Long id) {
        return courseMapper.selectById(id);
    }

    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectAll(course);
        return PageInfo.of(list);
    }

    public void insert(Course course) {
        course.setTime(DateUtil.now());
        courseMapper.insert(course);
    }

    public void update(Course course) {
        course.setTime(DateUtil.now());
        courseMapper.update(course);
    }

    public void deleteById(Long id) {
        courseMapper.deleteById(id);
    }

    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            this.deleteById(id);
        }
    }
}


