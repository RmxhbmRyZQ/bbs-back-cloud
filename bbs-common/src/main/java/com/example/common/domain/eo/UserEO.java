package com.example.common.domain.eo;

import lombok.Data;

@Data
public class UserEO {
    private Long uid;

    private String username;

    private String nickname;

    private String avatar;

    private String email;

    private Boolean emailVerified;

    private String registerIp;

    private String createTime;

    private Boolean banned;
}
