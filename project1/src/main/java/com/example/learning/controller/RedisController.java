package com.example.learning.controller;

import com.example.learning.dto.ApiResponse;
import com.example.learning.redis.RedisDemoService;
import com.example.learning.redis.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisDemoService redisDemoService;
    private final RedisDistributedLock distributedLock;

    @PostMapping("/string")
    public ApiResponse<String> setString(@RequestParam String key, @RequestParam String value) {
        redisDemoService.demoString(key, value);
        return ApiResponse.ok(redisDemoService.getString(key));
    }

    @GetMapping("/string/{key}")
    public ApiResponse<String> getString(@PathVariable String key) {
        return ApiResponse.ok(redisDemoService.getString(key));
    }

    @PostMapping("/hash/{key}")
    public ApiResponse<Map<Object, Object>> setHash(@PathVariable String key,
                                                     @RequestBody Map<String, String> fields) {
        redisDemoService.demoHash(key, fields);
        return ApiResponse.ok(redisDemoService.getHash(key));
    }

    @PostMapping("/list/{key}")
    public ApiResponse<List<String>> addList(@PathVariable String key,
                                              @RequestBody List<String> values) {
        redisDemoService.demoList(key, values.toArray(new String[0]));
        return ApiResponse.ok(redisDemoService.getList(key, 0, -1));
    }

    @PostMapping("/set/{key}")
    public ApiResponse<Set<String>> addSet(@PathVariable String key,
                                            @RequestBody List<String> members) {
        redisDemoService.demoSet(key, members.toArray(new String[0]));
        return ApiResponse.ok(redisDemoService.getSet(key));
    }

    @PostMapping("/zset/{key}")
    public ApiResponse<Set<String>> addZSet(@PathVariable String key,
                                              @RequestParam String member,
                                              @RequestParam double score) {
        redisDemoService.demoZSet(key, member, score);
        return ApiResponse.ok(redisDemoService.getTopScores(key, 10));
    }

    /** 分布式锁演示：同一资源串行执行 */
    @PostMapping("/lock-demo")
    public ApiResponse<String> lockDemo(@RequestParam(defaultValue = "resource-1") String resource) {
        String result = distributedLock.executeWithLock("lock:" + resource, 10, () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "在锁保护下完成操作: " + resource;
        });
        return ApiResponse.ok(result);
    }
}
