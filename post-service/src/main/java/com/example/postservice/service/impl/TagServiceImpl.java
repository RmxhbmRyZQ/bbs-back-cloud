package com.example.postservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.postservice.domain.po.Tag;
import com.example.postservice.service.TagService;
import com.example.postservice.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author RmxhbmRyZQ
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2025-04-19 18:04:14
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




