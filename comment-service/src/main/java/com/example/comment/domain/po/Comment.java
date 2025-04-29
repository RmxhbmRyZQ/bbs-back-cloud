package com.example.comment.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
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

    private static final long serialVersionUID = 1L;
}