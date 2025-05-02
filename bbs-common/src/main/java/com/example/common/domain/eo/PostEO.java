package com.example.common.domain.eo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostEO {
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
}
