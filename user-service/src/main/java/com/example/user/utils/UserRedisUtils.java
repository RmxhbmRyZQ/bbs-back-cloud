package com.example.user.utils;

import com.example.common.domain.dto.UserDTO;
import com.example.common.utils.RedisKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UserRedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value(("${jwt.accessTokenTTL}"))
    private long userCacheTTL;

    private static final DateTimeFormatter DEADLINE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 缓存用户信息
    public void cacheUser(UserDTO user) {
        String uidKey = RedisKeyUtils.getKeyUserUidKey(user.getUid().toString());
        String usernameKey = RedisKeyUtils.getKeyUserUsernameKey(user.getUsername());

        redisTemplate.opsForValue().set(uidKey, user, userCacheTTL, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(usernameKey, user.getUid(), userCacheTTL, TimeUnit.MILLISECONDS);
    }

    // 获取用户缓存（通过 uid）
    public UserDTO getUserByUid(String uid) {
        Object obj = redisTemplate.opsForValue().get(RedisKeyUtils.getKeyUserUidKey(uid));
        if (obj instanceof UserDTO) return (UserDTO) obj;
        return null;
    }

    // 获取用户缓存（通过 username）
    public UserDTO getUserByUsername(String username) {
        Object uidObj = redisTemplate.opsForValue().get(RedisKeyUtils.getKeyUserUsernameKey(username));
        if (uidObj == null) return null;
        return getUserByUid(uidObj.toString());
    }

    // 删除用户缓存（用于封禁、解封、修改用户信息后）
    public void deleteUserCache(String uid, String username) {
        redisTemplate.delete(RedisKeyUtils.getKeyUserUidKey(uid));
        redisTemplate.delete(RedisKeyUtils.getKeyUserUsernameKey(username));
    }

    public void deleteUserCache(String uid) {
        UserDTO user = getUserByUid(uid);
        if (user != null) {
            deleteUserCache(uid, user.getUsername());
        } else {
            redisTemplate.delete(RedisKeyUtils.getKeyUserUidKey(uid));
        }
    }

    // 访问后刷新 TTL（滑动过期）
    public void refreshUserTTL(String uid, String username) {
        redisTemplate.expire(RedisKeyUtils.getKeyUserUidKey(uid), userCacheTTL, TimeUnit.MILLISECONDS);
        redisTemplate.expire(RedisKeyUtils.getKeyUserUsernameKey(username), userCacheTTL, TimeUnit.MILLISECONDS);
    }
}
