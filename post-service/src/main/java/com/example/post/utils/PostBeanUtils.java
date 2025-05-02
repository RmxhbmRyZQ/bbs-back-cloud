package com.example.post.utils;

import cn.hutool.core.bean.BeanUtil;
import com.example.common.domain.dto.PostDTO;
import com.example.common.domain.dto.TagDTO;
import com.example.common.domain.dto.TagOptionDTO;
import com.example.common.domain.eo.PostEO;
import com.example.post.domain.po.Post;
import com.example.post.domain.po.Tag;
import com.example.post.domain.po.TagOption;

public class PostBeanUtils {
    public static PostDTO postDTO(Post post) {
        return BeanUtil.toBean(post, PostDTO.class);
    }

    public static Post post(PostDTO postDTO) {
        return BeanUtil.toBean(postDTO, Post.class);
    }

    public static TagDTO tagDTO(Tag tag) {
        return BeanUtil.toBean(tag, TagDTO.class);
    }

    public static TagOptionDTO tagOptionDTO(TagOption tagOption) {
        return BeanUtil.toBean(tagOption, TagOptionDTO.class);
    }

    public static Tag tag(TagDTO tagDTO) {
        return BeanUtil.toBean(tagDTO, Tag.class);
    }

    public static PostEO postEO(Post post) {
        return BeanUtil.toBean(post, PostEO.class);
    }

    public static PostEO postEO(PostDTO post) {
        return BeanUtil.toBean(post, PostEO.class);
    }
}
