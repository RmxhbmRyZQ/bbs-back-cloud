package com.example.monitor.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="sensitive_word")
@Data
public class SensitiveWord implements Serializable {
    private Integer id;

    private String word;

    private static final long serialVersionUID = 1L;
}