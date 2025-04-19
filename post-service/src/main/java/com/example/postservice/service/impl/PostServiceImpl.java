package com.example.postservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.postservice.domain.po.Post;
import com.example.postservice.service.PostService;
import com.example.postservice.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author RmxhbmRyZQ
* @description 针对表【post】的数据库操作Service实现
* @createDate 2025-04-19 18:04:14
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




