package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public interface UserService extends IService<User> {
    UserDTO getByUsername(String username);

    Role loadUserRoleByUid(String valueOf);

    void updateUserRole(Long uid, int rid);

    Boolean checkNicknameUnique(String nickname, UserBO userBO);

    Boolean correctPassword(BCryptPasswordEncoder passwordEncoder, UserBO userBO, String currentPassword);

    UserDTO getByUid(String uid);

    List<String> loadRoleAuthoritiesByRid(Integer rid);
}
