package com.example.userservice.controller;

import com.example.common.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 修改用户个人信息控制器
 */
@RestController
public class UserController {
    @GetMapping("/profile")
    public Response<Object> getProfile() {
        return null;
    }

    @GetMapping("/userProfile")
    public Response<Object> getUserProfile(@RequestParam String username) {
        return null;
    }

    @GetMapping("/userPosts")
    public Response<Object> getUserPosts(@RequestParam String username) {
        return null;
    }

    @GetMapping("/updateNickname")
    public Response<String> updateNickname(
            @RequestParam String nickname) {
        return null;
    }

    @PostMapping("/updatePassword")
    public Response<String> updatePassword(
            @RequestParam String confirmNewPassword) {
        return null;
    }

    @PostMapping("/updateAvatar")
    public Response<Object> updateAvatar(@RequestParam("file") MultipartFile avatar) {
        return null;
    }
}
