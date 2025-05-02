package com.example.post.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import com.example.common.domain.dto.TagDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName(value ="post")
@Data
@NoArgsConstructor
public class Post implements Serializable {
    private Long id;

    private Long uid;

    private String title;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    private Double priority;

    /**
     * 帖子状态类型。0-正常; 1-精华; 2-拉黑
     */
    private Integer status;

    /**
     * 帖子类型。0-正常; 1-置顶; 2-全局置顶
     */
    private Integer type;

    private Integer replyNumber;

    private Integer viewNumber;

    @TableField(fill = FieldFill.INSERT)
    private String lastCommentTime;

    private static final long serialVersionUID = 1L;
}