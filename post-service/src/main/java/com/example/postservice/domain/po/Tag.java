package com.example.postservice.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    private Integer id;

    private Integer optionId;

    private String label;

    private String name;

    private String icon;

    private String detail;

    private Long creator;

    private Date createTime;

    private static final long serialVersionUID = 1L;
}