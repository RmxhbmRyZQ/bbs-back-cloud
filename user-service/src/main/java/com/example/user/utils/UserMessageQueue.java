package com.example.user.utils;

import com.example.common.domain.eo.UserEO;
import com.example.common.utils.MessageQueueKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMessageQueue {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 插入用户到 ES（注册成功时）
     */
    public void sendInsert(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_INSERT,
                userEO
        );
    }

    /**
     * 更新用户昵称
     */
    public void sendUpdateNickname(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_NICKNAME,
                userEO
        );
    }

    /**
     * 更新用户头像
     */
    public void sendUpdateAvatar(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_AVATAR,
                userEO
        );
    }

    /**
     * 更新邮箱验证状态（邮箱激活）
     */
    public void sendUpdateEmailVerified(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_EMAIL_VERIFIED,
                userEO
        );
    }

    /**
     * 封禁用户
     */
    public void sendBannedUser(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_BANNED,
                userEO
        );
    }

    /**
     * 解封用户
     */
    public void sendUnbannedUser(UserEO userEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.USER_EXCHANGE,
                MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_UNBANNED,
                userEO
        );
    }
}
