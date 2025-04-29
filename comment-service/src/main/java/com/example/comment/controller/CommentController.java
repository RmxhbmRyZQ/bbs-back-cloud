package com.example.comment.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import com.example.api.client.PostClient;
import com.example.api.client.SensitiveClient;
import com.example.api.dto.UpdateComment;
import com.example.comment.domain.po.Comment;
import com.example.comment.service.CommentService;
import com.example.comment.utils.CommentBeanUtils;
import com.example.common.domain.dto.CommentDTO;
import com.example.common.response.Response;
import com.example.common.utils.SensitiveWordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final SensitiveClient sensitiveClient;

    private final PostClient postClient;

    @GetMapping("/getComments")
    public Response<Map<String, Object>> getComments(@RequestParam String pid) {
        List<CommentDTO> comments = commentService.getComments(pid);
        if (ObjectUtil.isNotNull(comments)) {
            List<CommentDTO> commentDTOS = new ArrayList<>();
            for (CommentDTO comment: comments) {
                comment.setContent(HtmlUtil.unescape(comment.getContent()));
                comment.setRepliesNum(commentService.getRepliesNumOfComment(comment.getId()));
                commentDTOS.add(comment);
            }
            return Response.success("获取评论列表成功", Map.of("comments", commentDTOS));
        }
        return Response.failed("获取评论列表失败");
    }

    @GetMapping("/getReplies")
    public Response<Object> getReplies(@RequestParam String pid, @RequestParam Integer parentId) {
        List<CommentDTO> replies = commentService.getReplies(pid, parentId);
        if (ObjectUtil.isNotNull(replies)) {
            for (CommentDTO reply: replies) {
                reply.setContent(HtmlUtil.unescape(reply.getContent()));
            }
            return Response.success("获取回复列表成功", Map.of("replies", replies));
        }
        return Response.failed("获取回复列表失败");
    }

    @PostMapping("/doComment")
    public Response<Map<String, Comment>> doComment(@RequestBody Comment comment) {
        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String commentContent = SensitiveWordUtils.stringSearchEx2Filter(comment.getContent(), sensitiveWords);
        comment.setContent(HtmlUtil.escape(commentContent));
        comment.setParentId(0);
        comment.setReplyId(0);

        boolean saved = commentService.save(comment);
        if (saved) {
            boolean updated = postClient.updateComment(new UpdateComment(comment.getPid(), comment.getCreateTime())).getData();
            if (updated) {
                comment.setContent(commentContent);
                return Response.success("评论成功", Map.of("comment", comment));
            }
        }
        return Response.failed("评论失败");
    }

    @PostMapping("/doReply")
    public Response<Map<String, Comment>> doReply(@RequestBody Comment comment) {
        List<String> sensitiveWords = sensitiveClient.getSensitiveWord().getData();
        String replyText = SensitiveWordUtils.stringSearchEx2Filter(comment.getContent(), sensitiveWords);
        comment.setContent(HtmlUtil.escape(replyText));

        boolean saved = commentService.save(comment);
        if (saved) {
            comment.setContent(replyText);
            return Response.success("回复成功", Map.of("reply", comment));
        }

        return Response.failed("回复失败");
    }
}
