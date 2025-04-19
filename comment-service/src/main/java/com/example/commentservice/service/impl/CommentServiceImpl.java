package com.example.commentservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.commentservice.domain.po.Comment;
import com.example.commentservice.service.CommentService;
import com.example.commentservice.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author RmxhbmRyZQ
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2025-04-19 18:04:43
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




