package com.example.comment.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.comment.domain.po.Comment;
import com.example.comment.service.CommentService;
import com.example.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/getComments")
    public Response<Map<String, Object>> getComments(@RequestParam String pid) {
        // 这里应该进行分页，不分页redis缓存也很难设计，需要前端能够进行分页
        List<Comment> comments = commentService.getComments(pid);
        if (ObjectUtil.isNotNull(comments)) {
            return Response.success("获取评论列表成功", Map.of("comments", comments));
        }
        return Response.failed("获取评论列表失败");
    }

    @GetMapping("/getReplies")
    public Response<Object> getReplies(@RequestParam String pid, @RequestParam Integer parentId) {
        // 这里同样的也需要进行分页
        List<Comment> replies = commentService.getReplies(pid, parentId);
        if (ObjectUtil.isNotNull(replies)) {
            return Response.success("获取回复列表成功", Map.of("replies", replies));
        }
        return Response.failed("获取回复列表失败");
    }

    @PostMapping("/doComment")
    public Response<Map<String, Comment>> doComment(@RequestBody Comment comment) {
        boolean saved = commentService.doComment(comment);
        if (saved) {
            return Response.success("评论成功", Map.of("comment", comment));
        }
        return Response.failed("评论失败");
    }

    @PostMapping("/doReply")
    public Response<Map<String, Comment>> doReply(@RequestBody Comment comment) {
        boolean saved = commentService.doReply(comment);
        if (saved) {
            return Response.success("回复成功", Map.of("reply", comment));
        }

        return Response.failed("回复失败");
    }
}
