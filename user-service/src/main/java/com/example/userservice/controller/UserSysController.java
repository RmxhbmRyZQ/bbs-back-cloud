package com.example.userservice.controller;

import com.example.common.response.Response;
import com.example.userservice.domain.po.Banned;
import com.example.userservice.domain.po.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-ctrl")
public class UserSysController {

    @GetMapping("/users")
    public Response<Map<String, List<User>>> getAllUser() {
        return null;
    }

    @PostMapping("/banUser")
    public Response<Object> banUser(@RequestBody Banned bannedUser) {
        return null;
    }

    @DeleteMapping("/banUser")
    public Response<Object> cancelBanUser(@RequestParam String uid) {
        return null;
    }
}
