package com.smartedu.mapper;

import com.smartedu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper // 添加Mapper注解
public interface UserMapper {

    User findByUsernameAndPassword(String username, String password);

}