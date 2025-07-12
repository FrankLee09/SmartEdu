package com.smartedu.mapper;

import com.smartedu.entity.Student;
import com.smartedu.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeacherMapper {

    List<Teacher> getAllTeachers();

    List<Teacher> selectAll(Teacher teacher);

    Teacher selectById(Long id);

    void insert(Teacher teacher);

    void update(Teacher teacher);

    void deleteById(Long id);

    @Select("select * from teachers where username = #{username}")
    Teacher selectByUsername(String username);
}
