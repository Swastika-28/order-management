package com.project.order_management.dto;

import lombok.*;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class RechargeDto {
    private Long custId;
    private String token;
    private BigDecimal amount;
}
