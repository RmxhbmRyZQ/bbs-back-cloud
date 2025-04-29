package com.example.user.mapper;

import com.example.user.domain.po.Authority;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorityMapper extends BaseMapper<Authority> {
    public List<String> loadAuthoritiesByRid(@Param("rid") String rid);

    List<String> loadRoleAuthoritiesByRid(Integer rid);
}




