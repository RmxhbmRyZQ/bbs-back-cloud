package com.example.common.domain.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer id;

    private Long pid;

    private Long fromUid;

    private Long toUid;

    private String content;

    private Integer parentId;

    private Integer replyId;

    private String createTime;

    private String fromUsername;

    private String fromNickname;

    private String fromAvatar;

    private String toUsername;

    private String toNickname;

    private Integer repliesNum;
}
