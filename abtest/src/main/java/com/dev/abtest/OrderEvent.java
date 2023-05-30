package com.dev.abtest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderEvent {
    private Long userId;
    private Long orderId;
    private BigDecimal amount;
}
