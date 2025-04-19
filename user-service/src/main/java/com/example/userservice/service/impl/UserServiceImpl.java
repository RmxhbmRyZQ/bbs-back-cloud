package com.example.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain/po.User;
import com.example.userservice.service.UserService;
import com.example.userservice.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author RmxhbmRyZQ
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-04-19 17:45:36
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




