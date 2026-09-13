package com.project.order_management.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponseDto {
    private Long orderId;
    private BigDecimal totalAmount;
    private String statusCode;
    private String statusMessage;
}
