package com.example.userservice.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    private Long uid;

    private Integer id;

    private String username;

    private String nickname;

    private String password;

    private String avatar;

    private String email;

    private Integer emailVerified;

    private String salt;

    private String registerIp;

    private Date createTime;

    private Date updateTime;

    private String updatedBy;

    private Integer banned;

    private Integer deleted;

    private static final long serialVersionUID = 1L;
}