package com.example.commentservice.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    private Integer id;

    private Long pid;

    private Long fromUid;

    private Long toUid;

    private String content;

    private Integer parentId;

    private Integer replyId;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}