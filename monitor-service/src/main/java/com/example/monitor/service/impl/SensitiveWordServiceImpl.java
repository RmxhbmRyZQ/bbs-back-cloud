package com.example.monitor.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.RedisKeyUtils;
import com.example.monitor.domain.po.SensitiveWord;
import com.example.monitor.mapper.SensitiveWordMapper;
import com.example.monitor.service.SensitiveWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord>
    implements SensitiveWordService {

    private final String sensitiveObjsKey = RedisKeyUtils.getSensitiveObjsKey();

    private final String sensitiveWordsKey = RedisKeyUtils.getSensitiveWordsKey();

    private final SensitiveWordMapper sensitiveWordMapper;

    private final RedisTemplate<String, SensitiveWord> redisTemplate;

    private final RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public List<SensitiveWord> getSensitiveWords() {
        List<SensitiveWord> sensitiveWords;

        Boolean hasKey = redisTemplate.hasKey(sensitiveObjsKey);
        if (Boolean.TRUE.equals(hasKey)) {
            sensitiveWords = redisTemplate.opsForList().range(sensitiveObjsKey, 0, -1);
            if (ObjectUtil.isNotNull(sensitiveWords)) {
                return sensitiveWords;
            }
        }

        sensitiveWords = sensitiveWordMapper.selectList(null);
        sensitiveWords.forEach(sensitiveWord -> {
            redisTemplate.opsForList().rightPush(sensitiveObjsKey, sensitiveWord);
        });
        return sensitiveWords;
    }

    @Override
    public List<String> getSensitiveStringWords() {
        List<String> sensitiveWords;
        Boolean hasKey = stringRedisTemplate.hasKey(sensitiveWordsKey);
        if (Boolean.TRUE.equals(hasKey)) {
            sensitiveWords = stringRedisTemplate.opsForList().range(sensitiveWordsKey, 0, -1);
            if (ObjectUtil.isNotNull(sensitiveWords)) {
                return sensitiveWords;
            }
        }

        getSensitiveWords().forEach(sensitiveWord -> {
            stringRedisTemplate.opsForList().rightPush(sensitiveWordsKey, sensitiveWord.getWord());
        });
        sensitiveWords = stringRedisTemplate.opsForList().range(sensitiveWordsKey, 0, -1);
        return sensitiveWords;
    }
}




