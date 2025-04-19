package com.example.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName authority
 */
@TableName(value ="authority")
@Data
public class Authority implements Serializable {
    private Integer aid;

    private String name;

    private String authorities;

    private static final long serialVersionUID = 1L;
}