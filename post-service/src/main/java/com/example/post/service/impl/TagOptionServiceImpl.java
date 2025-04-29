package com.example.post.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.domain.dto.TagOptionDTO;
import com.example.common.utils.RedisKeyUtils;
import com.example.post.domain.po.TagOption;
import com.example.post.service.TagOptionService;
import com.example.post.mapper.TagOptionMapper;
import com.example.post.service.TagService;
import com.example.post.utils.PostBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagOptionServiceImpl extends ServiceImpl<TagOptionMapper, TagOption>
    implements TagOptionService{

    private final String tagOptionsKey = RedisKeyUtils.getTagOptionsKey();

    private final TagOptionMapper tagOptionMapper;

    private final TagService tagService;

    private final RedisTemplate<String, TagOptionDTO> redisTemplate;

    @Override
    public List<TagOptionDTO> getAllTagOptions() {
        List<TagOptionDTO> tagOptions;
        Boolean hasKey = redisTemplate.hasKey(tagOptionsKey);
        if (Boolean.TRUE.equals(hasKey)) {
            tagOptions = redisTemplate.opsForList().range(tagOptionsKey, 0, -1);
            if (ObjectUtil.isNotNull(tagOptions)) {
                return tagOptions;
            }
        }

        List<TagOption> tagOptionList = tagOptionMapper.selectList(null);
        List<TagOptionDTO> result = new ArrayList<>();
        tagOptionList.forEach(tagOption -> {
            TagOptionDTO tagOptionDTO = PostBeanUtils.tagOptionDTO(tagOption);
            redisTemplate.opsForList().rightPush(tagOptionsKey, tagOptionDTO);
            result.add(tagOptionDTO);
        });

        return result;
    }

    @Override
    public Map<String, Object> getTagsAndOptions() {
        return Map.of("tags", tagService.getAllTag(), "tagOptions", getAllTagOptions());
    }
}




