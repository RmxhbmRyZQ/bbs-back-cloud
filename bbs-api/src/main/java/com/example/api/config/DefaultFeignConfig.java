package com.example.api.config;

import com.example.common.domain.bo.UserBO;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return template -> {
            try{
                Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if ((user instanceof UserBO userBO)) {
                    template.header("User-ID", userBO.getUserDTO().getUid().toString());
                }
            } catch (Exception e) {

            }
        };
    }
}