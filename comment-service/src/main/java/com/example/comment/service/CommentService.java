package com.example.comment.service;

import com.example.comment.domain.po.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.domain.dto.CommentDTO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    List<CommentDTO> getComments(String pid);

    Integer getRepliesNumOfComment(Integer commentId);

    List<CommentDTO> getReplies(String pid, Integer parentId);
}
