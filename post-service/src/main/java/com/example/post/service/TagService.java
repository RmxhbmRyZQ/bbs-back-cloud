package com.example.post.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.domain.dto.TagDTO;
import com.example.post.domain.po.PostTag;
import com.example.post.domain.po.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TagService extends IService<Tag> {

    List<TagDTO> getAllTag();

    IPage<PostTag> selectPostTagPage(IPage<PostTag> page, QueryWrapper<PostTag> queryWrapper);

    Boolean isTagUsed(Tag tag);

    void updatePostTag(UpdateWrapper<PostTag> updateWrapper);
}
