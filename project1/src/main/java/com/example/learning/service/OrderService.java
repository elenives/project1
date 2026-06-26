package com.example.learning.service;

import com.example.learning.dto.CreateOrderRequest;
import com.example.learning.entity.Order;
import com.example.learning.entity.User;
import com.example.learning.repository.OrderRepository;
import com.example.learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单服务 — 演示 @Transactional 事务管理
 *
 * 事务概念：
 * - ACID：原子性、一致性、隔离性、持久性
 * - 下单时扣减余额 + 创建订单在同一事务中，任一失败则全部回滚
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public List<Order> listByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> listByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * 创建订单并扣减用户余额 — 事务保证原子性
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("余额不足，当前余额: " + user.getBalance());
        }

        user.setBalance(user.getBalance().subtract(request.getAmount()));
        userRepository.save(user);

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProduct(request.getProduct());
        order.setAmount(request.getAmount());
        order.setStatus("PENDING");

        Order saved = orderRepository.save(order);
        log.info("订单创建成功: orderId={}, 扣减金额={}", saved.getId(), request.getAmount());
        return saved;
    }

    /**
     * 演示事务回滚：故意抛出异常，余额和订单都不会保存
     */
    @Transactional(rollbackFor = Exception.class)
    public void demoRollback(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
        throw new RuntimeException("模拟异常，触发事务回滚");
    }
}
