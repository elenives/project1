package com.example.learning.controller;

import com.example.learning.dto.ApiResponse;
import com.example.learning.dto.CreateOrderRequest;
import com.example.learning.entity.Order;
import com.example.learning.mq.MessageProducer;
import com.example.learning.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MessageProducer messageProducer;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> listByUser(@PathVariable Long userId) {
        return ApiResponse.ok(orderService.listByUserId(userId));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<Order>> listByStatus(@PathVariable String status) {
        return ApiResponse.ok(orderService.listByStatus(status));
    }

    /** 创建订单（事务：扣余额 + 写订单） */
    @PostMapping
    public ApiResponse<Order> create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        try {
            messageProducer.sendAsync("order", "新订单: id=" + order.getId() + ", product=" + order.getProduct());
        } catch (Exception e) {
            // RocketMQ 未启动时不影响下单主流程
        }
        return ApiResponse.ok(order);
    }

    /** 演示事务回滚（余额不会被扣减） */
    @PostMapping("/demo-rollback")
    public ApiResponse<String> demoRollback(@RequestParam Long userId,
                                            @RequestParam BigDecimal amount) {
        try {
            orderService.demoRollback(userId, amount);
        } catch (RuntimeException e) {
            return ApiResponse.ok("事务已回滚，余额未变化: " + e.getMessage());
        }
        return ApiResponse.fail("未触发回滚");
    }
}
