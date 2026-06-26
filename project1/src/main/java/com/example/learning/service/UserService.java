package com.example.learning.service;

import com.example.learning.entity.User;
import com.example.learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务 — 演示依赖注入（构造器注入 + @RequiredArgsConstructor）
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + id));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("邮箱不存在: " + email));
    }

    public List<User> searchByUsername(String keyword) {
        return userRepository.findByUsernameContaining(keyword);
    }

    public List<User> findRichUsers(BigDecimal minBalance) {
        return userRepository.findByBalanceGreaterThan(minBalance);
    }

    public User createUser(String username, String email, BigDecimal balance) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setBalance(balance);
        return userRepository.save(user);
    }
}
