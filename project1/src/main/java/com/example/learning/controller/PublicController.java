package com.example.learning.controller;

import com.example.learning.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** 公开接口，无需 X-Token 鉴权 */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("status", "UP");
        info.put("project", "springboot-learning");
        info.put("auth", "其他 /api/** 接口需在 Header 添加 X-Token: learning-token-123");
        return ApiResponse.ok(info);
    }

    @GetMapping("/topics")
    public ApiResponse<Map<String, String>> topics() {
        Map<String, String> topics = new LinkedHashMap<>();
        topics.put("spring-mvc", "UserController / OrderController");
        topics.put("di", "构造器注入 @RequiredArgsConstructor");
        topics.put("transaction", "OrderService.createOrder @Transactional");
        topics.put("aop", "LogAspect 记录 Service 耗时");
        topics.put("interceptor", "AuthInterceptor 鉴权 X-Token");
        topics.put("database", "schema.sql 索引 + JPA 查询");
        topics.put("redis", "/api/redis/** 五种数据结构 + 分布式锁");
        topics.put("rocketmq", "/api/mq/send 生产者，MessageConsumer 消费者");
        return ApiResponse.ok(topics);
    }
}
