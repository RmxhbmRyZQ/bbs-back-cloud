package com.example.post.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName tag
 */
@TableName(value ="tag")
@Data
@NoArgsConstructor
public class Tag implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer optionId;

    private String label;

    private String name;

    private String icon;

    private String detail;

    private Long creator;

    private String createTime;

    private static final long serialVersionUID = 1L;

    public Tag(Integer optionId, String name, String label, String icon, String detail) {
        this.optionId = optionId;
        this.name = name;
        this.label = label;
        this.icon = icon;
        this.detail = detail;
    }
}