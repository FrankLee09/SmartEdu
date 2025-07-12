package com.smartedu.mapper;

import com.smartedu.entity.Submission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubmissionMapper {
    List<Submission> getAllSubmissions();

    List<Submission> selectAll(Submission submission);
    Submission selectById(Long id);
    int insert(Submission submission);
    int update(Submission submission);
    int deleteById(Long id);
}