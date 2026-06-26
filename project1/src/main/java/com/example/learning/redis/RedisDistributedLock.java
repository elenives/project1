package com.example.learning.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁（SET NX EX + Lua 脚本释放）
 *
 * 原理：
 * 1. SET key uniqueValue NX EX ttl — 仅当 key 不存在时设置，并带过期时间
 * 2. 释放时用 Lua 脚本校验 value 是否为本线程持有，防止误删他人锁
 * 3. 生产环境可改用 Redisson（支持看门狗自动续期）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then "
                    + "return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;

    public boolean tryLock(String lockKey, String lockValue, long expireSeconds) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expireSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String lockKey, String lockValue) {
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
    }

    public <T> T executeWithLock(String lockKey, long expireSeconds, Supplier<T> action) {
        String lockValue = UUID.randomUUID().toString();
        if (!tryLock(lockKey, lockValue, expireSeconds)) {
            throw new IllegalStateException("获取锁失败: " + lockKey);
        }
        try {
            return action.get();
        } finally {
            unlock(lockKey, lockValue);
        }
    }
}
