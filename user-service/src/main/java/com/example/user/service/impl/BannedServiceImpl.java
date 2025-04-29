package com.example.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.user.domain.po.Banned;
import com.example.user.service.BannedService;
import com.example.user.mapper.BannedMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BannedServiceImpl extends ServiceImpl<BannedMapper, Banned>
    implements BannedService{

    private final BannedMapper bannedMapper;

    @Override
    public void insertBannedHistory(Long uid, String createTime, String deadline, String reason) {
        bannedMapper.insertBannedHistory(uid, createTime, deadline, reason);
    }
}




