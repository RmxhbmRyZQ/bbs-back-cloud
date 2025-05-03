package com.example.post.utils;

import com.example.common.domain.eo.PostEO;
import com.example.common.utils.MessageQueueKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMessageQueue {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送“插入帖子”消息到 ES
     */
    public void sendInsert(PostEO postEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.POST_EXCHANGE,
                MessageQueueKeyUtils.POST_ROUTING_KEY_INSERT,
                postEO
        );
    }

    /**
     * 发送“更新帖子标题”消息到 ES
     */
    public void sendUpdateTitle(PostEO postEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.POST_EXCHANGE,
                MessageQueueKeyUtils.POST_ROUTING_KEY_UPDATE_TITLE,
                postEO
        );
    }

    /**
     * 发送“更新帖子内容”消息到 ES
     */
    public void sendUpdateContent(PostEO postEO) {
        rabbitTemplate.convertAndSend(
                MessageQueueKeyUtils.POST_EXCHANGE,
                MessageQueueKeyUtils.POST_ROUTING_KEY_UPDATE_CONTENT,
                postEO
        );
    }
}
