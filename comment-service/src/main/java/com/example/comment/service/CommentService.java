package com.example.comment.service;

import com.example.comment.domain.po.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CommentService extends IService<Comment> {

    List<Comment> getComments(String pid);

    Integer getRepliesNumOfComment(Integer commentId);

    List<Comment> getReplies(String pid, Integer parentId);

    Boolean doComment(Comment comment);

    Boolean doReply(Comment comment);
}
