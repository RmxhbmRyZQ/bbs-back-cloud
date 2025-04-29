package com.example.comment.utils;

import cn.hutool.core.bean.BeanUtil;
import com.example.comment.domain.po.Comment;
import com.example.common.domain.dto.CommentDTO;

public class CommentBeanUtils {
    public static CommentDTO commentDTO(Comment comment) {
        return BeanUtil.toBean(comment, CommentDTO.class);
    }
}
