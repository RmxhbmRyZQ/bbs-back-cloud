package com.example.commentservice.controller;

import com.example.commentservice.domain.po.Comment;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.Map;

@RestController
public class CommentController {

    @GetMapping("/getComments")
    public Response<Map<String, Object>> getComments(@RequestParam String pid) {
        return null;
    }

    @GetMapping("/getReplies")
    public Response<Object> getReplies(@RequestParam String pid, @RequestParam Integer parentId) {
        return null;
    }

    @PostMapping("/doComment")
    public Response<Map<String, Comment>> doComment(@RequestBody Comment comment) {
        return null;
    }

    @PostMapping("/doReply")
    public Response<Map<String, Comment>> doReply(@RequestBody Comment comment) {
        return null;
    }
}
