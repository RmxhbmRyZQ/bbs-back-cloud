package com.example.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName banned
 */
@TableName(value ="banned")
@Data
public class Banned implements Serializable {
    private Integer id;

    private Long uid;

    private String deadline;

    private String reason;

    private static final long serialVersionUID = 1L;
}