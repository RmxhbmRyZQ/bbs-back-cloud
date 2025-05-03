package com.example.post.utils;

import cn.hutool.core.bean.BeanUtil;
import com.example.common.domain.dto.PostDTO;
import com.example.common.utils.RedisKeyUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class PostRedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final Duration duration = Duration.ofHours(1);

    private final DefaultRedisScript<Long> updateViewScript = new DefaultRedisScript<>();
    private final DefaultRedisScript<Long> updateReplyScript = new DefaultRedisScript<>();

    @PostConstruct
    public void init() {
        updateViewScript.setScriptText("""
            if redis.call('EXISTS', KEYS[1]) == 0 then
                return nil
            end
            local view = redis.call('HGET', KEYS[1], 'viewNumber') or 0
            view = view + ARGV[1]
            redis.call('HSET', KEYS[1], 'viewNumber', view)
            return view
        """);
        updateViewScript.setResultType(Long.class);

        updateReplyScript.setScriptText("""
            -- 如果 key 不存在，直接返回 nil
            if redis.call('EXISTS', KEYS[1]) == 0 then
                return nil
            end
                    
            local reply = redis.call('HGET', KEYS[1], 'replyNumber') or 0
            reply = reply + ARGV[2]
            redis.call('HSET', KEYS[1], 'replyNumber', reply)
            redis.call('HSET', KEYS[1], 'lastCommentTime', ARGV[1])
            return reply
        """);
        updateReplyScript.setResultType(Long.class);
    }

    // 缓存 PostDTO 到 Redis Hash
    public void cachePostDetail(PostDTO postDTO) {
        String key = RedisKeyUtils.getPostDetailKey(postDTO.getId().toString());
        Map<String, Object> map = BeanUtil.beanToMap(postDTO, false, true);
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, duration);
    }

    // 从 Redis Hash 获取 PostDTO
    public PostDTO getPostDetailFromCache(String postId) {
        String key = RedisKeyUtils.getPostDetailKey(postId);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        if (map == null || map.isEmpty()) return null;
        return BeanUtil.toBean(map, PostDTO.class);
    }

    // 删除 Redis 中帖子缓存
    public void deletePostDetail(String postId) {
        redisTemplate.delete(RedisKeyUtils.getPostDetailKey(postId));
    }

    // 浏览量 +1（Lua 脚本原子更新）
    public Long incrViewNumber(String postId) {
        String key = RedisKeyUtils.getPostDetailKey(postId);
        return redisTemplate.execute(updateViewScript, List.of(key), 1);
    }

    // 回复数 +1，更新最后评论时间（Lua 脚本原子更新）
    public Long updateReplyAndTime(String postId, String lastCommentTime) {
        String key = RedisKeyUtils.getPostDetailKey(postId);
        return redisTemplate.execute(updateReplyScript, List.of(key), lastCommentTime, 1);
    }

    // 添加延迟更新 Key（如果不存在则添加，返回是否成功添加）
    public boolean addPostDelayKey(String postId, long delayMinutes) {
        String delayKey = RedisKeyUtils.getPostDelayKey(postId);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(delayKey, "1", Duration.ofMinutes(delayMinutes));
        return Boolean.TRUE.equals(success);
    }

    // 删除延迟更新 Key
    public void removePostDelayKey(String postId) {
        String delayKey = RedisKeyUtils.getPostDelayKey(postId);
        redisTemplate.delete(delayKey);
    }
}
