package com.example.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "avatar")
@Data
@Component
public class AvatarProperties {
    private String remotePrefix;
    private String remoteSuffix;
    private String prefix;
}
