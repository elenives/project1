package com.example.learning.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    private Long userId;
    private String product;
    private BigDecimal amount;
}
