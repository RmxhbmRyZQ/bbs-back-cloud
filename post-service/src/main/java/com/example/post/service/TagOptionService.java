package com.example.post.service;

import com.example.common.domain.dto.TagOptionDTO;
import com.example.post.domain.po.TagOption;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface TagOptionService extends IService<TagOption> {

    List<TagOptionDTO> getAllTagOptions();

    Map<String, Object> getTagsAndOptions();
}
