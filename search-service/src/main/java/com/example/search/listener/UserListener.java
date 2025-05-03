package com.example.search.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.common.domain.eo.UserEO;
import com.example.common.utils.MessageQueueKeyUtils;
import com.example.common.utils.elastic.ElasticUserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserListener {

    private final ElasticsearchClient elasticsearchClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_INSERT, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_INSERT
    ))
    public void handleInsert(UserEO userEO) {
        try {
            ElasticUserUtils.insertUserToEs(elasticsearchClient, userEO);
            log.info("【ES】插入用户成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】插入用户失败：uid={}", userEO.getUid(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_UPDATE_NICKNAME, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_NICKNAME
    ))
    public void handleUpdateNickname(UserEO userEO) {
        try {
            ElasticUserUtils.updateUserNicknameInEs(elasticsearchClient, userEO);
            log.info("【ES】更新昵称成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】更新昵称失败：uid={}", userEO.getUid(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_UPDATE_AVATAR, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_AVATAR
    ))
    public void handleUpdateAvatar(UserEO userEO) {
        try {
            ElasticUserUtils.updateUserAvatarInEs(elasticsearchClient, userEO);
            log.info("【ES】更新头像成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】更新头像失败：uid={}", userEO.getUid(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_UPDATE_EMAIL_VERIFIED, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_EMAIL_VERIFIED
    ))
    public void handleEmailVerified(UserEO userEO) {
        try {
            ElasticUserUtils.updateUserEmailVerifiedInEs(elasticsearchClient, userEO);
            log.info("【ES】更新邮箱状态成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】更新邮箱状态失败：uid={}", userEO.getUid(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_UPDATE_BANNED, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_BANNED
    ))
    public void handleBanned(UserEO userEO) {
        try {
            ElasticUserUtils.updateUserBannedStatusInEs(elasticsearchClient, userEO);
            log.info("【ES】封禁用户成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】封禁用户失败：uid={}", userEO.getUid(), e);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.USER_QUEUE_UPDATE_UNBANNED, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.USER_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.USER_ROUTING_KEY_UPDATE_UNBANNED
    ))
    public void handleUnbanned(UserEO userEO) {
        try {
            ElasticUserUtils.updateUserBannedStatusInEs(elasticsearchClient, userEO);
            log.info("【ES】解封用户成功：uid={}", userEO.getUid());
        } catch (IOException e) {
            log.error("【ES】解封用户失败：uid={}", userEO.getUid(), e);
        }
    }

}
