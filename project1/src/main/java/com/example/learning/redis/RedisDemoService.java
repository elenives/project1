package com.example.learning.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 数据结构演示
 *
 * String  — 缓存、计数器、分布式锁 value
 * Hash    — 对象字段存储（如用户信息）
 * List    — 消息队列、最新列表
 * Set     — 去重集合（如标签）
 * ZSet    — 有序集合（如排行榜）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDemoService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    /** String：缓存 + 计数器 */
    public void demoString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);
        Long count = stringRedisTemplate.opsForValue().increment("counter:visits");
        log.info("String demo: key={}, count={}", key, count);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /** Hash：存储对象字段 */
    public void demoHash(String key, Map<String, String> fields) {
        stringRedisTemplate.opsForHash().putAll(key, fields);
    }

    public Map<Object, Object> getHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /** List：队列操作 */
    public void demoList(String key, String... values) {
        for (String v : values) {
            stringRedisTemplate.opsForList().rightPush(key, v);
        }
    }

    public List<String> getList(String key, long start, long end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    /** Set：去重 */
    public void demoSet(String key, String... members) {
        stringRedisTemplate.opsForSet().add(key, members);
    }

    public Set<String> getSet(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /** ZSet：排行榜 */
    public void demoZSet(String key, String member, double score) {
        stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    public Set<String> getTopScores(String key, long topN) {
        return stringRedisTemplate.opsForZSet().reverseRange(key, 0, topN - 1);
    }
}
