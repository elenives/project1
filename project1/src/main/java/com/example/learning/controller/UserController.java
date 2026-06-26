package com.example.learning.controller;

import com.example.learning.dto.ApiResponse;
import com.example.learning.entity.User;
import com.example.learning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * MVC 控制器 — 演示 RESTful API
 * Model: Entity/DTO | View: JSON 响应 | Controller: 本类
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.ok(userService.listAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getById(id));
    }

    @GetMapping("/email/{email}")
    public ApiResponse<User> getByEmail(@PathVariable String email) {
        return ApiResponse.ok(userService.getByEmail(email));
    }

    @GetMapping("/search")
    public ApiResponse<List<User>> search(@RequestParam String keyword) {
        return ApiResponse.ok(userService.searchByUsername(keyword));
    }

    @GetMapping("/rich")
    public ApiResponse<List<User>> richUsers(@RequestParam(defaultValue = "500") BigDecimal minBalance) {
        return ApiResponse.ok(userService.findRichUsers(minBalance));
    }

    @PostMapping
    public ApiResponse<User> create(@RequestBody User user) {
        return ApiResponse.ok(userService.createUser(
                user.getUsername(), user.getEmail(), user.getBalance()));
    }
}
