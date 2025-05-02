package com.example.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

@TableName(value ="authority")
@Data
public class Authority implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer aid;

    private String name;

    private List<String> authorities;

    private static final long serialVersionUID = 1L;
}