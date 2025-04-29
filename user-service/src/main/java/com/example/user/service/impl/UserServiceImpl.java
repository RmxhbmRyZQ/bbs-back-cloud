package com.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.api.client.FileClient;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.user.domain.po.Authority;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import com.example.user.mapper.AuthorityMapper;
import com.example.user.mapper.RoleMapper;
import com.example.user.service.UserService;
import com.example.user.mapper.UserMapper;
import com.example.user.utils.UserBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final AuthorityMapper authorityMapper;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, Object> redisTemplate;

    private final FileClient fileClient;
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public UserDTO getByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return null;
        }

        Role role = getRoleByUid(String.valueOf(user.getUid()));
        user.setRole(role);

        if (role == null) {
            return BeanUtil.toBean(user, UserDTO.class);
        }

        Authority authority = getAuthoritiesByRid(String.valueOf(role.getRid()));
        user.setAuthority(authority);
        return UserBeanUtils.userDTO(user);
    }

    @Override
    public UserDTO getByUid(String uid) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUid, uid).select(User::getUsername, User::getUid, User::getAvatar, User::getNickname);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return null;
        }

        Role role = getRoleByUid(String.valueOf(user.getUid()));
        user.setRole(role);

        if (role == null) {
            return BeanUtil.toBean(user, UserDTO.class);
        }

        Authority authority = getAuthoritiesByRid(String.valueOf(role.getRid()));
        user.setAuthority(authority);
        return UserBeanUtils.userDTO(user);
    }

    @Override
    public List<String> loadRoleAuthoritiesByRid(Integer rid) {
        return authorityMapper.loadRoleAuthoritiesByRid(rid);
    }

    @Override
    public Role loadUserRoleByUid(String uid) {
        return roleMapper.loadRoleByUid(uid);
    }

    @Override
    public void updateUserRole(Long uid, int rid) {
        userMapper.updateUserRole(uid, rid);
    }

    @Override
    public Boolean checkNicknameUnique(String nickname, UserBO userBO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", nickname);
        Long result = userMapper.selectCount(queryWrapper);
        return result.intValue() == 0;
    }

    @Override
    public Boolean correctPassword(BCryptPasswordEncoder passwordEncoder, UserBO userBO, String currentPassword) {
        return passwordEncoder.matches(currentPassword, userBO.getPassword());
    }

    private Role getRoleByUid(String uid) {
        return roleMapper.loadRoleByUid(uid);
    }

    private Authority getAuthoritiesByRid(String rid) {
        Authority authority = new Authority();
        List<String> authorities = authorityMapper.loadAuthoritiesByRid(rid);
        authority.setAuthorities(authorities);
        return authority;
    }
}




