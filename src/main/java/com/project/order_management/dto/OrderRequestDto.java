package com.project.order_management.dto;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private Long customerId;
    private Long productId;
    private Integer quantity;

}
