package com.example.searchservice.controller;

import com.example.postservice.domain.po.Post;
import com.example.userservice.domain.po.User;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @GetMapping("/post")
    public Response<Map<String, List<Post>>> searchPostsByKey(@RequestParam String keyword) {
        return null;
    }

    @GetMapping("/user")
    public Response<Map<String, List<User>>> searchUsersByKey(@RequestParam String keyword) {
        return null;
    }

    @PostMapping("/addPosts")
    public Response<String> insertAllPostToEs() {
        return null;
    }

    @PostMapping("/addUsers")
    public Response<String> insertAllUserToEs() {
        return null;
    }
}
