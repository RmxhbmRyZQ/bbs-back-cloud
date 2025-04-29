package com.example.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String tokenHeader;
    private String tokenPrefix;
    private String secret;
    private Long accessTokenTTL;
    private Long refreshTokenTTL;
    private Long autoRefreshTtl;
}

