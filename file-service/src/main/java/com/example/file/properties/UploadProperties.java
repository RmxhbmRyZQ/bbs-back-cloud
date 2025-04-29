package com.example.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "upload")
@Data
@Component
public class UploadProperties {
    private String avatarPath;
    private String staticPath;
    private String avatarMapperPath;
    private String staticMapperPath;
}
