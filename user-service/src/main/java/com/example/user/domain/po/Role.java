package com.example.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="role")
@Data
public class Role implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer rid;

    private String name;

    private String alias;

    private Integer index;

    private static final long serialVersionUID = 1L;
}