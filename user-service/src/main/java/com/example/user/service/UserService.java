package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public interface UserService extends IService<User> {
    UserDTO getByUsername(String username);

    UserDTO getByUid(String uid);

    Role loadUserRoleByUid(String valueOf);

    void updateUserRole(Long uid, int rid);

    Boolean checkNicknameUnique(String nickname);

    Boolean correctPassword(PasswordEncoder passwordEncoder, UserBO userBO, String currentPassword);

    List<String> loadRoleAuthoritiesByRid(Integer rid);
}
