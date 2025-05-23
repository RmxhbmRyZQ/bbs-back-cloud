package com.example.api.client;

import com.example.api.config.DefaultFeignConfig;
import com.example.api.dto.UpdateComment;
import com.example.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "post-service", configuration = DefaultFeignConfig.class)
public interface PostClient {
    @PostMapping("/postComment")
    Response<Boolean> updateComment(@RequestBody UpdateComment updateComment);

    @GetMapping("/poseCount")
    Response<Long> getTotalPostNumber();

    @GetMapping("/post/page")
    public Response<Object> getPostPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);
}
