package com.example.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName banned
 */
@TableName(value ="banned")
@Data
public class Banned implements Serializable {
    private Integer id;

    private Long uid;

    private Date deadline;

    private String reason;

    private static final long serialVersionUID = 1L;
}