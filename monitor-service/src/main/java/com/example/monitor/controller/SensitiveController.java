package com.example.monitor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.response.Response;
import com.example.common.utils.RedisKeyUtils;
import com.example.monitor.domain.po.SensitiveWord;
import com.example.monitor.service.SensitiveWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/sys-ctrl")
@RequiredArgsConstructor
public class SensitiveController {

    private final String sensitiveObjsKey = RedisKeyUtils.getSensitiveObjsKey();

    private final SensitiveWordService sensitiveWordService;

    private final RedisTemplate<String, SensitiveWord> redisTemplate;

    @PostMapping("/sensitiveWord")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Response<Map<String, SensitiveWord>> addSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        boolean saved = sensitiveWordService.save(sensitiveWord);
        if (saved) {
            redisTemplate.opsForList().rightPushIfPresent(sensitiveObjsKey, sensitiveWord);
            return Response.success("新增敏感词成功", Map.of("sensitiveWord", sensitiveWord));
        } else return Response.failed("新增敏感词失败");
    }

    @GetMapping("/sensitiveWord")
    public Response<Map<String, List<SensitiveWord>>> getSensitiveWords() {
        List<SensitiveWord> sensitiveWords = sensitiveWordService.getSensitiveWords();
        return Response.success("获取敏感词成功", Map.of("sensitiveWords", sensitiveWords));
    }

    @GetMapping("/sensitive")
    public Response<List<String>> getSensitive() {
        List<String> sensitive = sensitiveWordService.getSensitiveStringWords();
        return Response.success("获取敏感词成功", sensitive);
    }

    @PutMapping("/sensitiveWord")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Response<Map<String, SensitiveWord>> updateSensitiveWord(@RequestBody SensitiveWord sensitiveWord) {
        QueryWrapper<SensitiveWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", sensitiveWord.getId());
        if (!sensitiveWordService.exists(queryWrapper)) {
            return Response.failed("该敏感词不存在");
        }

        boolean updated = sensitiveWordService.updateById(sensitiveWord);
        if (updated) {
            // 这样写的话不能删除敏感字，只能修改，不然会有很多位置是空的
            redisTemplate.opsForList().set(sensitiveObjsKey, sensitiveWord.getId() - 1, sensitiveWord);
            return Response.success("更新敏感词成功", Map.of("sensitiveWord", sensitiveWord));
        } else return Response.failed("更新敏感词失败");
    }
}
