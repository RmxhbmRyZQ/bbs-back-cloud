package com.example.post.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.domain.dto.TagDTO;
import com.example.common.utils.RedisKeyUtils;
import com.example.post.domain.po.PostTag;
import com.example.post.domain.po.Tag;
import com.example.post.domain.po.TagOption;
import com.example.post.mapper.PostTagMapper;
import com.example.post.mapper.TagOptionMapper;
import com.example.post.service.TagService;
import com.example.post.mapper.TagMapper;
import com.example.post.utils.PostBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    private final String tagsKey = RedisKeyUtils.getTagsKey();

    private final TagMapper tagMapper;

    private final TagOptionMapper tagOptionMapper;

    private final PostTagMapper postTagMapper;

    private final RedisTemplate<String, TagDTO> redisTemplate;

    @Override
    public List<TagDTO> getAllTag() {
        List<TagDTO> tags;
        Boolean hasKey = redisTemplate.hasKey(tagsKey);
        if (Boolean.TRUE.equals(hasKey)) {
            tags = redisTemplate.opsForList().range(tagsKey, 0, -1);
            if (ObjectUtil.isNotNull(tags)) {
                return tags;
            }
        }

        List<Tag> tagList = tagMapper.selectList(null);
        List<TagDTO> allTag = new ArrayList<>();

        tagList.forEach(tag -> {
            TagDTO tagDTO = PostBeanUtils.tagDTO(tag);
            QueryWrapper<TagOption> tagOptionQueryWrapper = new QueryWrapper<>();
            tagOptionQueryWrapper.eq("id", tag.getOptionId());
            TagOption tagOption = tagOptionMapper.selectOne(tagOptionQueryWrapper);
            tagDTO.setTagOption(PostBeanUtils.tagOptionDTO(tagOption));
            allTag.add(tagDTO);
            redisTemplate.opsForList().rightPush(tagsKey, tagDTO);
        });
        return allTag;
    }

    @Override
    public IPage<PostTag> selectPostTagPage(IPage<PostTag> page, QueryWrapper<PostTag> queryWrapper) {
        return postTagMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Boolean isTagUsed(Tag tag) {
        QueryWrapper<PostTag> postTagExistQueryWrapper = new QueryWrapper<>();
        postTagExistQueryWrapper.eq("tag_label", tag.getLabel());
        return postTagMapper.exists(postTagExistQueryWrapper);
    }

    @Override
    public void updatePostTag(UpdateWrapper<PostTag> updateWrapper) {
        postTagMapper.update(updateWrapper);
    }
}




