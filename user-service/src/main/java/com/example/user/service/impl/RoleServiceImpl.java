package com.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.domain.po.Role;
import com.example.user.service.RoleService;
import com.example.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author RmxhbmRyZQ
* @description 针对表【role】的数据库操作Service实现
* @createDate 2025-04-19 17:45:36
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




