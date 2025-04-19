package com.example.postservice.controller;
import com.example.common.response.Response;
import com.example.postservice.domain.po.Post;
import com.example.postservice.domain.po.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    /**
     * 发布帖子
     * @param post 帖子
     * @return result
     */
    @PostMapping("/post")
    public Response<Map<String, String>> doPost(@RequestBody Post post) {
        return null;
    }

    /**
     * 修改帖子内容
     * @param post 帖子
     * @return
     */
    @PutMapping("/postContent")
    public Response<Map<String, String>> doEditContent(@RequestBody Post post) {
        return null;
    }

    @PutMapping("/postTitle")
    public Response<Map<String, String>> doEditTitle(@RequestParam String pid, @RequestParam String title) {
        return null;
    }

    /**
     * 获取各种帖子列表
     * @param node 帖子列表类型
     * @param page 帖子所处列表的当前页码
     * @return result
     */
    @GetMapping("/list")
    public Response<Map<String, Object>> getPostList(@RequestParam String node, @RequestParam(required = false) Integer page) {
        return null;
    }

    /**
     * 获取帖子详情
     * @param pid 帖子ID
     * @return result
     */
    @GetMapping("/postInfo")
    public Response<Map<String, Object>> getPostInfo(@RequestParam String pid) {
        return null;
    }

    @PutMapping("/tagOfPost")
    public Response<Map<String, List<Tag>>> changeTagOfPost(@RequestBody Post post) {
        return null;
    }
}
