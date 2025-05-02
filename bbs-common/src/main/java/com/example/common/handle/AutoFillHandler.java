package com.example.common.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.common.utils.ConstantUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 字段自动填充处理类。
 */
public class AutoFillHandler implements MetaObjectHandler {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ConstantUtils.DATE_FORMAT);

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "createTime", String.class, LocalDateTime.now().format(dateTimeFormatter));
        strictInsertFill(metaObject, "lastCommentTime", String.class, LocalDateTime.now().format(dateTimeFormatter));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", String.class, LocalDateTime.now().format(dateTimeFormatter));
    }
}
