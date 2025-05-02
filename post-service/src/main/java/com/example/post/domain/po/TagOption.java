package com.example.post.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @TableName tag_option
 */
@TableName(value ="tag_option")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagOption implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String label;

    private String name;

    private static final long serialVersionUID = 1L;
}