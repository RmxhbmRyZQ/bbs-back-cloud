package com.example.security.service.impl;

import com.example.api.client.UserClient;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = userClient.getUserProfile(username).getData();
        if (userDTO == null) {
            throw new UsernameNotFoundException("请检查用户名是否输入正确");
        }
        return new UserBO(userDTO);
    }
}
