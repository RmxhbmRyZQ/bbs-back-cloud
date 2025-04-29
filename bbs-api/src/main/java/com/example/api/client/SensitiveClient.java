package com.example.api.client;

import com.example.api.config.DefaultFeignConfig;
import com.example.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "monitor-service", configuration = DefaultFeignConfig.class)
public interface SensitiveClient {
    @GetMapping("/sensitive")
    public Response<List<String>> getSensitiveWord();
}
