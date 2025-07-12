package com.smartedu.mapper;

import com.smartedu.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
    List<File> getAllFiles();

    List<File> selectAll(File file);

    File selectById(Long id);

    void insert(File file);

    void update(File file);

    void deleteById(Long id);

    void deleteByUrl(String fileUrl);
}
