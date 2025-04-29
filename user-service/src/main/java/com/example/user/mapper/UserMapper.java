package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.domain.po.User;

/**
* @author RmxhbmRyZQ
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-04-19 17:45:36
* @Entity com.example.userservice.domain/po.User
*/
public interface UserMapper extends BaseMapper<User> {

    int insertUserRole(Long uid, Integer rid);

    void updateUserRole(Long uid, Integer rid);
}




