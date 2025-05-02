package com.example.comment.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long pid;

    private Long fromUid;

    private Long toUid;

    private String content;

    private Integer parentId;

    private Integer replyId;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(exist = false)
    private String fromUsername;

    @TableField(exist = false)
    private String fromNickname;

    @TableField(exist = false)
    private String fromAvatar;

    @TableField(exist = false)
    private String toUsername;

    @TableField(exist = false)
    private String toNickname;

    private Integer repliesNum;

    private static final long serialVersionUID = 1L;
}