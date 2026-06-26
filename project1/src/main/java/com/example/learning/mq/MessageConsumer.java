package com.example.learning.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消费者
 *
 * selectorExpression 对应 Producer 发送时的 Tag，实现消息过滤
 * consumerGroup 同一组内消息负载均衡（每条消息只被一个实例消费）
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "rocketmq.consumer.enabled", havingValue = "true", matchIfMissing = true)
@RocketMQMessageListener(
        topic = MessageProducer.TOPIC,
        consumerGroup = "learning-consumer-group",
        selectorExpression = "order || notify"
)
public class MessageConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("[RocketMQ Consumer] 收到消息: {}", message);
        // 此处处理业务，如更新订单状态、发送通知等
    }
}
