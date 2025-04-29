package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.domain.dto.BannedDTO;
import com.example.user.domain.po.Banned;

public interface BannedService extends IService<Banned> {

    void insertBannedHistory(Long uid, String createTime, String deadline, String reason);
}
