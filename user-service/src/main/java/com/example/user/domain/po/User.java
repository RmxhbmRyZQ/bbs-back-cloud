package com.example.user.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import jakarta.validation.constraints.Email;
import lombok.Data;

@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long uid;

    private Integer id;

    private String username;

    private String nickname;

    private String password;

    private String avatar;

    @Email
    private String email;

    private Boolean emailVerified;

    private String salt;

    private String registerIp;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

    private String updatedBy;

    private Boolean banned;

    @TableLogic
    private Boolean deleted;

    /**
     * 用户具有的权限
     */
    @TableField(exist = false)
    private Authority authority;

    /**
     * 用户的角色信息
     */
    @TableField(exist = false)
    private Role role;

    private static final long serialVersionUID = 1L;
}