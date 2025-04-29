package com.example.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.monitor.domain.po.SensitiveWord;

import java.util.List;

/**
* @author RmxhbmRyZQ
* @description 针对表【sensitive_word】的数据库操作Service
* @createDate 2025-04-19 20:30:49
*/
public interface SensitiveWordService extends IService<SensitiveWord> {

    List<SensitiveWord> getSensitiveWords();

    List<String> getSensitiveStringWords();
}
