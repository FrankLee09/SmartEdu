package com.smartedu.mapper;

import com.smartedu.entity.Resourze;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourzeMapper {
    List<Resourze> getAllResources();

    List<Resourze> selectAll(Resourze resourze);

    Resourze selectById(Long id);

    void insert(Resourze resourze);

    void update(Resourze resourze);

    void deleteById(Long id);
}
