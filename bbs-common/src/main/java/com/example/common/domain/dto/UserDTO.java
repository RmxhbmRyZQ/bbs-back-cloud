package com.example.common.domain.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long uid;

    private Integer id;

    private String username;

    private String nickname;

    private String password;

    private String avatar;

    private String email;

    private Boolean emailVerified;

    private String registerIp;

    private String createTime;

    private Boolean banned;

    private String deadline;

    private AuthorityDTO authority;

    private RoleDTO role;
}
