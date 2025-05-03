package com.example.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.domain.bo.UserBO;
import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.ConstantUtils;
import com.example.user.domain.po.Authority;
import com.example.user.domain.po.Banned;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import com.example.user.mapper.AuthorityMapper;
import com.example.user.mapper.RoleMapper;
import com.example.user.service.BannedService;
import com.example.user.service.UserService;
import com.example.user.mapper.UserMapper;
import com.example.user.utils.UserRedisUtils;
import com.example.user.utils.UserBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern(ConstantUtils.DATE_FORMAT);

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final AuthorityMapper authorityMapper;

    private final BannedService bannedService;

    private final UserRedisUtils userRedisUtils;

    @Override
    public UserDTO getByUsername(String username) {
        UserDTO userDTO = userRedisUtils.getUserByUsername(username);

        if (userDTO == null) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, username);
            User user = userMapper.selectOne(queryWrapper);
            userDTO = getUserDTO(user);
            checkBanned(userDTO, userDTO.getUid().toString());
        } else {
            userRedisUtils.refreshUserTTL(userDTO.getUid().toString(), userDTO.getUsername());
        }

        return userDTO;
    }

    @Override
    public UserDTO getByUid(String uid) {
        UserDTO userDTO = userRedisUtils.getUserByUid(uid);

        if (userDTO == null) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUid, uid);
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) return null;
            userDTO = getUserDTO(user);
            checkBanned(userDTO, uid);
        } else {
            userRedisUtils.refreshUserTTL(uid, userDTO.getUsername());
        }

        return userDTO;
    }

    private UserDTO getUserDTO(User user) {
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

    private void checkBanned(UserDTO userDTO, String uid) {
        if (Boolean.TRUE.equals(userDTO.getBanned())) {
            LambdaQueryWrapper<Banned> bannedLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bannedLambdaQueryWrapper.eq(Banned::getUid, uid);
            Banned banned = bannedService.getOne(bannedLambdaQueryWrapper);

            LocalDateTime deadline = LocalDateTime.parse(banned.getDeadline(), DEADLINE_FORMATTER);
            if (LocalDateTime.now().isAfter(deadline)) {
                LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(User::getUid, uid).set(User::getBanned, false);
                update(updateWrapper);
                userDTO.setBanned(false);
            } else {
                userDTO.setDeadline(banned.getDeadline());
            }
        }
        userRedisUtils.cacheUser(userDTO); // 缓存到 Redis
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
    public Boolean checkNicknameUnique(String nickname) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", nickname);
        Long result = userMapper.selectCount(queryWrapper);
        return result.intValue() == 0;
    }

    @Override
    public Boolean correctPassword(PasswordEncoder passwordEncoder, UserBO userBO, String currentPassword) {
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




