package com.example.security.config;

import com.example.security.filter.UserHeaderAuthenticationFilter;
import com.example.security.handle.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
public class SecurityBeanConfig {
    @Bean
    public LogoutHandler logoutHandler() {
        return new LogoutHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandlerImpl();
    }

    @Bean
    public NoLoginHandler noLoginHandler() {
        return new NoLoginHandler();
    }

    @Bean
    public NoPermissionHandler noPermissionHandler() {
        return new NoPermissionHandler();
    }

    @Bean
    public UserHeaderAuthenticationFilter userHeaderAuthenticationFilter() {
        return new UserHeaderAuthenticationFilter();
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
