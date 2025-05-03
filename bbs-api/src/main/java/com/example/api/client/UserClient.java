package com.example.api.client;

import com.example.api.config.DefaultFeignConfig;
import com.example.common.domain.dto.UserDTO;
import com.example.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service", configuration = DefaultFeignConfig.class)
public interface UserClient {
    @GetMapping("/userProfile")
    public Response<UserDTO> getUserProfile(@RequestParam("username") String username);

    @GetMapping("/userId")
    public Response<UserDTO> getUserProfileByUid(@RequestParam("uid") String uid);

    @GetMapping("/userCount")
    public Response<Long> getTotalUserNumber();

    @GetMapping("/user/page")
    public Response<Object> getUserPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);
}
