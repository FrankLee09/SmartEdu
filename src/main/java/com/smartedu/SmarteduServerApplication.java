package com.smartedu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.smartedu.mapper")
@EnableCaching
public class SmarteduServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmarteduServerApplication.class, args);
    }

}
