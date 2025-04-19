package com.example.postservice.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
    private Long id;

    private Long uid;

    private String title;

    private String content;

    private Date createTime;

    private Double priority;

    private Integer status;

    private Integer type;

    private Integer replyNumber;

    private Integer viewNumber;

    private Date lastCommentTime;

    private static final long serialVersionUID = 1L;
}