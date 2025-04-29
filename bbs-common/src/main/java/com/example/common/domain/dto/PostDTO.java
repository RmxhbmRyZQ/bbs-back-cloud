package com.example.common.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PostDTO {
    private Long id;

    private Long uid;

    private String title;

    private String content;

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

    private String lastCommentTime;

    /**
     * 帖子标签
     */
    @TableField(exist = false)
    private List<TagDTO> tags;

    /**
     * 作者用户名
     */
    @TableField(exist = false)
    private String author;

    /**
     * 作者昵称
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 作者头像
     */
    @TableField(exist = false)
    private String avatar;
}
