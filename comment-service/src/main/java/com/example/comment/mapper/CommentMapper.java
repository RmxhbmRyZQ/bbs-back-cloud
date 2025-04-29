package com.example.comment.mapper;

import com.example.comment.domain.po.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getComments(String pid);

    Integer getRepliesNumOfComment(Integer parentId);

    List<Comment> getReplies(String pid, Integer parentId);
}




