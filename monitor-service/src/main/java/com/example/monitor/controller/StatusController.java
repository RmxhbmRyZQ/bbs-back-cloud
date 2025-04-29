package com.example.monitor.controller;

import com.example.api.client.PostClient;
import com.example.api.client.UserClient;
import com.example.common.response.Response;
import com.example.common.utils.RedisKeyUtils;
import com.example.monitor.config.SystemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StatusController {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserClient userClient;

    private final PostClient postClient;

    @GetMapping("/systemInfo")
    public Response<Object> getSystemInfo() {
        Map<String, String> memory = SystemUtils.getMemory();
        Map<String, Object> processor = SystemUtils.getProcessor();
        Map<String, Integer> forum = getForumInfo().getData();

        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("memory", memory);
        systemInfo.put("cpu", processor);
        systemInfo.put("forum", forum);
        return Response.success("获取系统信息成功", systemInfo);
    }

    @GetMapping("/forumInfo")
    public Response<Map<String, Integer>> getForumInfo() {
        String userNumberKey = RedisKeyUtils.getUserNumberKey();
        String postNumberKey = RedisKeyUtils.getPostNumberKey();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(userNumberKey))) {
            Long userNumber = userClient.getTotalUserNumber().getData();
            redisTemplate.opsForValue().set(userNumberKey, userNumber);
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(postNumberKey))) {
            Long postNumber = postClient.getTotalPostNumber().getData();
            redisTemplate.opsForValue().set(postNumberKey, postNumber);
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("userNumber", (Integer) redisTemplate.opsForValue().get(userNumberKey));
        map.put("postNumber", (Integer) redisTemplate.opsForValue().get(postNumberKey));
        return Response.success("获取社区数据成功", map);
    }
}
