package com.example.post.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @TableName tag_option
 */
@TableName(value ="tag_option")
@Data
@AllArgsConstructor
public class TagOption implements Serializable {
    private Integer id;

    private String label;

    private String name;

    private static final long serialVersionUID = 1L;
}