package com.example.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

@TableName(value ="authority")
@Data
public class Authority implements Serializable {
    private Integer aid;

    private String name;

    private List<String> authorities;

    private static final long serialVersionUID = 1L;
}