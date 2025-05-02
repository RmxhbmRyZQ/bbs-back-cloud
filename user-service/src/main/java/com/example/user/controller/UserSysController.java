package com.example.user.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.response.Response;
import com.example.common.utils.elastic.ElasticUserUtils;
import com.example.user.domain.po.Authority;
import com.example.user.domain.po.Banned;
import com.example.user.domain.po.Role;
import com.example.user.domain.po.User;
import com.example.user.service.BannedService;
import com.example.user.service.UserService;
import com.example.user.utils.UserRedisUtils;
import com.example.user.utils.UserBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys-ctrl")
@RequiredArgsConstructor
public class UserSysController {

    private final UserService userService;

    private final BannedService bannedService;

    private final ElasticsearchClient elasticsearchClient;

    private final UserRedisUtils userRedisUtils;

    @GetMapping("/users")
    public Response<Map<String, List<User>>> getAllUser() {
        List<User> users = userService.list();  // 这里应该分页
        users.forEach(user -> {
            Role role = userService.loadUserRoleByUid(String.valueOf(user.getUid()));
            Authority authority = new Authority();
            authority.setAuthorities(userService.loadRoleAuthoritiesByRid(role.getRid()));

            user.setPassword(null);
            user.setRole(role);
            user.setAuthority(authority);
        });

        return Response.success("获取所有用户成功", Map.of("users", users));
    }

    @PostMapping("/banUser")
    @Transactional
    public Response<Object> banUser(@RequestBody Banned bannedUser) {
        boolean saved = bannedService.save(bannedUser);
        if (saved) {
            Long uid = bannedUser.getUid();
            String reason = bannedUser.getReason();
            String deadline = bannedUser.getDeadline();
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            bannedService.insertBannedHistory(uid, now, deadline, reason);

            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("uid", uid).set("banned", true);
            userService.update(null, userUpdateWrapper);

//            UserDTO userDTO = userService.getByUid(uid.toString());
//            userDTO.setBanned(true);
//            userDTO.setDeadline(deadline);
//            redisUtils.cacheUser(userDTO);
            userRedisUtils.deleteUserCache(uid.toString());

            User user = new User();
            user.setUid(uid);
            user.setBanned(true);
            try {
                ElasticUserUtils.updateUserBannedStatusInEs(elasticsearchClient, UserBeanUtils.userEO(user));
            } catch (IOException e) {
                return Response.success("封禁成功");
            }
            return Response.success("封禁成功");
        } else return Response.failed("封禁失败");
    }

    @DeleteMapping("/banUser")
    public Response<Object> cancelBanUser(@RequestParam String uid) {
        QueryWrapper<Banned> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);

        boolean removed = bannedService.remove(queryWrapper);
        if (removed) {
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("uid", uid).set("banned", false);
            userService.update(null, userUpdateWrapper);

//            UserDTO userDTO = userService.getByUid(uid);
//            userDTO.setBanned(false);
//            userDTO.setDeadline(null);
//            redisUtils.cacheUser(userDTO);
            userRedisUtils.deleteUserCache(uid.toString());

            User user = new User();
            user.setUid(Long.valueOf(uid));
            user.setBanned(false);
            try {
                ElasticUserUtils.updateUserBannedStatusInEs(elasticsearchClient, UserBeanUtils.userEO(user));
            } catch (IOException e) {
                return Response.success("解除封禁成功");
            }
            return Response.success("解除封禁成功");
        } else return Response.failed("解除封禁失败");
    }
}
