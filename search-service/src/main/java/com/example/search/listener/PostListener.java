package com.example.search.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.common.domain.eo.PostEO;
import com.example.common.utils.elastic.ElasticPostUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import com.example.common.utils.MessageQueueKeyUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostListener {

    private final ElasticsearchClient elasticsearchClient;

    /**
     * 监听“插入帖子”消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.POST_QUEUE_INSERT, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.POST_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.POST_ROUTING_KEY_INSERT
    ))
    public void handleInsert(PostEO postEO) {
        try {
            ElasticPostUtils.insertPostToEs(elasticsearchClient, postEO);
            log.info("【ES】插入帖子成功：postId={}", postEO.getId());
        } catch (IOException e) {
            log.error("【ES】插入帖子失败：{}", postEO.getId(), e);
        }
    }

    /**
     * 监听“更新标题”消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.POST_QUEUE_UPDATE_TITLE, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.POST_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.POST_ROUTING_KEY_UPDATE_TITLE
    ))
    public void handleUpdateTitle(PostEO postEO) {
        try {
            ElasticPostUtils.updatePostTitleInEs(elasticsearchClient, postEO);
            log.info("【ES】更新标题成功：postId={}", postEO.getId());
        } catch (IOException e) {
            log.error("【ES】更新标题失败：{}", postEO.getId(), e);
        }
    }

    /**
     * 监听“更新内容”消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MessageQueueKeyUtils.POST_QUEUE_UPDATE_CONTENT, durable = "true"),
            exchange = @Exchange(value = MessageQueueKeyUtils.POST_EXCHANGE, type = "direct"),
            key = MessageQueueKeyUtils.POST_ROUTING_KEY_UPDATE_CONTENT
    ))
    public void handleUpdateContent(PostEO postEO) {
        try {
            ElasticPostUtils.updatePostContentInEs(elasticsearchClient, postEO);
            log.info("【ES】更新内容成功：postId={}", postEO.getId());
        } catch (IOException e) {
            log.error("【ES】更新内容失败：{}", postEO.getId(), e);
        }
    }
}
