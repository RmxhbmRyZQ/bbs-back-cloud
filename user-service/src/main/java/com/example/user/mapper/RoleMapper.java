package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.domain.po.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author RmxhbmRyZQ
* @description 针对表【role】的数据库操作Mapper
* @createDate 2025-04-19 17:45:36
* @Entity com.example.userservice.domain/po.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    Role loadRoleByUid(@Param("uid") String uid);

}




