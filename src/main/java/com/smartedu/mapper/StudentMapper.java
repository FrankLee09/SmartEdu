package com.smartedu.mapper;

import com.smartedu.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper // 添加Mapper注解
public interface StudentMapper {

    List<Student> getAllstudents();

    List<Student> selectAll(Student student);

    Student selectById(Long id);

    int insert(Student student);

    int update(Student student);

    int deleteById(Long id);

    @Select("select * from students where username = #{username}")
    Student selectByUsername(String username);



}
