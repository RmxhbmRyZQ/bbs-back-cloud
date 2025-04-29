package com.example.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.domain.po.Banned;

public interface BannedMapper extends BaseMapper<Banned> {

    void insertBannedHistory(Long uid, String createTime, String deadline, String reason);
}




