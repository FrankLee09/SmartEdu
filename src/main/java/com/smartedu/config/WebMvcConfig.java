package com.smartedu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /files/** 到本地files文件夹
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/files/");
    }
}
