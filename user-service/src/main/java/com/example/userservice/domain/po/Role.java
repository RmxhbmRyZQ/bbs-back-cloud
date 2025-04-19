package com.example.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName role
 */
@TableName(value ="role")
@Data
public class Role implements Serializable {
    private Integer rid;

    private String name;

    private String alias;

    private Integer index;

    private static final long serialVersionUID = 1L;
}