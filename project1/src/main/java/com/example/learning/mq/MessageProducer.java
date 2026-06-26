package com.example.learning.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * RocketMQ 生产者
 *
 * 核心概念：
 * - NameServer：路由注册中心，Broker 和 Producer/Consumer 通过它发现彼此
 * - Topic：消息主题，逻辑分类
 * - Tag：子分类，用于 Consumer 过滤
 * - Producer Group / Consumer Group：生产者/消费者组，保证负载均衡和故障转移
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    public static final String TOPIC = "learning-topic";

    private final RocketMQTemplate rocketMQTemplate;

    /** 同步发送 — 等待 Broker 确认 */
    public void sendSync(String tag, String body) {
        String destination = TOPIC + ":" + tag;
        rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(body).build());
        log.info("[RocketMQ Producer] 同步发送成功 destination={}, body={}", destination, body);
    }

    /** 异步发送 — 不阻塞当前线程 */
    public void sendAsync(String tag, String body) {
        String destination = TOPIC + ":" + tag;
        rocketMQTemplate.asyncSend(destination, body, new org.apache.rocketmq.client.producer.SendCallback() {
            @Override
            public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                log.info("[RocketMQ Producer] 异步发送成功: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable e) {
                log.error("[RocketMQ Producer] 异步发送失败", e);
            }
        });
    }
}
