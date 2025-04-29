package com.example.file.config;

import com.example.file.properties.UploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadProperties.getStaticMapperPath() + "**").addResourceLocations("file:\\" + uploadProperties.getStaticPath());
        registry.addResourceHandler(uploadProperties.getAvatarMapperPath() + "**").addResourceLocations("file:\\" + uploadProperties.getAvatarPath());
    }
}
