//package com.smartedu.controller;
//
//import com.smartedu.entity.User;
//import com.smartedu.mapper.UserMapper;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@CrossOrigin(origins = "*")
//public class LoginController {
//
//    private final UserMapper userMapper;
//
//    // 通过构造器注入Mapper
//    public LoginController(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }
//
//    @PostMapping("/login")
//    public User login(@RequestBody LoginRequest request) {
//        // 直接调用Mapper进行数据库查询
//        return userMapper.findByUsernameAndPassword(
//                request.getUsername(),
//                request.getPassword()
//        );
//    }
//
//    // 内部类用于接收请求参数
//    static class LoginRequest {
//        private String username;
//        private String password;
//
//        public String getUsername() { return username; }
//        public void setUsername(String username) { this.username = username; }
//        public String getPassword() { return password; }
//        public void setPassword(String password) { this.password = password; }
//    }
//}
//
