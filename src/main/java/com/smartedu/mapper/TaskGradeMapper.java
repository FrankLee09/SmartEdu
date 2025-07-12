package com.smartedu.mapper;

import com.smartedu.entity.TaskGrade;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskGradeMapper {

    List<TaskGrade> getAllGrades();

    List<TaskGrade> selectAll(TaskGrade taskGrade);

    TaskGrade selectById(Long id);

    int insert(TaskGrade taskGrade);

    int update(TaskGrade taskGrade);

    int deleteById(Long id);

    List<TaskGrade> selectByStudentId(Long id);
    List<TaskGrade> selectTop7ByIdDesc(Long id);

}
