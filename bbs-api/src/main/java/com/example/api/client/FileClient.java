package com.example.api.client;

import com.example.api.config.DefaultFeignConfig;
import com.example.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "file-service", configuration = DefaultFeignConfig.class)
public interface FileClient {
    @PostMapping("/upload/init/avatar")
    public Response<String> initAvatar(@RequestParam("random") String random, @RequestParam("uid") Long uid);

    @PostMapping(value = "/upload/avatar", consumes = "multipart/form-data")
    public Response<String> uploadAvatar(@RequestPart("file") MultipartFile avatar, @RequestParam("uid") String uid);
}
