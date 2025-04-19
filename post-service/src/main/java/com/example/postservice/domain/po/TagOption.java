package com.example.postservice.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName tag_option
 */
@TableName(value ="tag_option")
@Data
public class TagOption implements Serializable {
    private Integer id;

    private String label;

    private String name;

    private static final long serialVersionUID = 1L;
}