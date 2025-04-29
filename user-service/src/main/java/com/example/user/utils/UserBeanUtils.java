package com.example.user.utils;

import cn.hutool.core.bean.BeanUtil;
import com.example.common.domain.dto.AuthorityDTO;
import com.example.common.domain.dto.RoleDTO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.domain.eo.UserEO;
import com.example.user.domain.po.User;

public class UserBeanUtils {
    public static UserDTO userDTO(User user) {
        UserDTO userDTO = BeanUtil.toBean(user, UserDTO.class);
        userDTO.setRole(BeanUtil.toBean(user.getRole(), RoleDTO.class));
        userDTO.setAuthority(BeanUtil.toBean(user.getAuthority(), AuthorityDTO.class));
        return userDTO;
    }

    public static UserEO userEO(User user) {
        return BeanUtil.toBean(user, UserEO.class);
    }

    public static UserEO userEO(UserDTO userDTO) {
        return BeanUtil.toBean(userDTO, UserEO.class);
    }
}
