package com.example.security.config;

import com.example.security.properties.JwtProperties;
import com.example.security.utils.JwtUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("jwt.token-header")
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityUtilsConfig {
    @Bean
    public JwtUtils jwtUtils(JwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }
}
