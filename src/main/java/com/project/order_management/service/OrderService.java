package com.project.order_management.service;

import com.project.order_management.dto.OrderRequestDto;
import com.project.order_management.dto.OrderResponseDto;
import com.project.order_management.exception.OrderProcessingException;
import com.project.order_management.repo.OrderProcessingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderProcessingRepo orderProcessingRepo;
    private static final String SUCCESS="SUCCESS";

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Executing OrderService::createOrder method.");
        log.debug(orderRequestDto.toString());

        Map<String, Object> response = orderProcessingRepo.executeProcessOrder(orderRequestDto.getCustomerId(), orderRequestDto.getProductId(), orderRequestDto.getQuantity());
        Long orderId= (Long) response.get("p_order_id");
        BigDecimal amount=(BigDecimal) response.get("p_total_amount");
        String statusCode=response.get("p_status_code").toString();
        String statusMsg=(String) response.get("p_status_message");
        if(!SUCCESS.equalsIgnoreCase(statusCode)){
            log.error("Order Processing Failed. Status={} Message={} ",statusCode,statusMsg);
            throw new OrderProcessingException("Order Failed"+statusMsg);
        }

        log.info("Exiting OrderService::createOrder.");
        return OrderResponseDto.builder()
                .orderId(orderId)
                .totalAmount(amount)
                .statusMessage(statusMsg)
                .statusCode(statusCode)
                .build();

    }



}
