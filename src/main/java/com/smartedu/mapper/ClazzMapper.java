package com.smartedu.mapper;
import com.smartedu.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper // 添加Mapper注解
public interface ClazzMapper {

    List<Clazz> getAllclasses();
    Clazz selectById(Long id);
    List<Clazz> selectAll(Clazz clazz);
    int insert(Clazz clazz);
    int update(Clazz clazz);
    int deleteById(Long id);


}
