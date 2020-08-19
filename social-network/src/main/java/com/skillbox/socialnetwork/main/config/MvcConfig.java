package com.skillbox.socialnetwork.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
//                .addResourceLocations("file://" + uploadPath + "/img/"); //для деплоя на серв
                .addResourceLocations("file:" + uploadPath + "/img/"); //для локального хранилища
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
