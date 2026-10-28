package com.project.order_management.service;

import com.project.order_management.dto.OrderRequestDto;
import com.project.order_management.dto.OrderResponseDto;
import com.project.order_management.dto.RechargeDto;
import com.project.order_management.exception.OrderProcessingException;
import com.project.order_management.model.Customer;
import com.project.order_management.model.Token;
import com.project.order_management.model.TokenId;
import com.project.order_management.repo.CustomerRepository;
import com.project.order_management.repo.OrderProcessingRepo;
import com.project.order_management.repo.TokenRepository;
import com.project.order_management.service.common.EmailService;
import com.project.order_management.service.common.TokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderProcessingRepo orderProcessingRepo;
    private final CustomerRepository customerRepository;
    private final TokenRepository rechargeRepo;
    private final JavaMailSender javaMailSender;
    private static final String SUCCESS = "SUCCESS";
    private final TokenService tokenService;
    private final EmailService emailService;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) throws MessagingException {
        log.info("Executing OrderService::createOrder method.");
        log.debug(orderRequestDto.toString());

        Map<String, Object> response = orderProcessingRepo.executeProcessOrder(orderRequestDto.getCustomerId(), orderRequestDto.getProductId(), orderRequestDto.getQuantity());
        Long orderId = (Long) response.get("p_order_id");
        BigDecimal amount = (BigDecimal) response.get("p_total_amount");
        String statusCode = response.get("p_status_code").toString();
        String statusMsg = (String) response.get("p_status_message");

        if (!SUCCESS.equalsIgnoreCase(statusCode)) {
            log.error("Order Processing Failed. Status={} Message={} ", statusCode, statusMsg);


            if ("Insufficient customer wallet balance.".equals(statusMsg) && "INSUFFICIENT_FUNDS".equals(statusCode)) {
                String token = UUID.randomUUID().toString();

                Customer customerDto = customerRepository.findById(orderRequestDto.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Customer not found"));


                tokenService.saveToken(customerDto, token);
                String content = ("Dear " + customerDto.getName() + ", your recent payment was declined due to insufficient funds. Your current balance is " + customerDto.getWallet_balance() + " please update your account balance or payment method to avoid service disruption." +
                        "This is your one-time recharge token: " + token);
                String subject = ("Payment Failed: Insufficient Account Balance");

                emailService.sendEmail(customerDto.getEmail(), subject, content);
                throw new OrderProcessingException("Order Failed " + statusMsg);
            }
        }

        log.info("Exiting OrderService::createOrder.");
        return OrderResponseDto.builder()
                .orderId(orderId)
                .totalAmount(amount)
                .statusMessage(statusMsg)
                .statusCode(statusCode)
                .build();

    }


    @Transactional
    public String rechargeWithToken(RechargeDto rechargeDto) {

        Token token = rechargeRepo.findByCustomerAndToken(rechargeDto.getCustId(), rechargeDto.getToken())
                .orElseThrow(() -> new RuntimeException("Token not found"));


        if (token.isUsed()) {
            throw new RuntimeException("This recharge token has already been used.");
        }

        checkPrivileged(rechargeDto.getCustId(), rechargeDto.getToken());
        token.setUsed(true);
        rechargeRepo.save(token);

        updateBalance(rechargeDto);
        return "Recharge successful and order completed!";
    }


    public BigDecimal checkBalance(Long custId, String token) {
        customerRepository.findById(custId)
                .orElseThrow(() -> new OrderProcessingException("Invalid customer ID "));
        rechargeRepo.findByCustomerAndToken(custId, token)
                .orElseThrow(() -> new OrderProcessingException("Not a valid token"));

        return fetchBalance(custId);
    }


    private BigDecimal fetchBalance(Long custId) {
        Optional<Customer> customer = customerRepository.findById(custId);
        BigDecimal balance = customer.map(c -> c.getWallet_balance()).orElse(null);
        return balance;
    }

    private void updateBalance(RechargeDto rechargeDto) {
        Customer customer = customerRepository.findById(rechargeDto.getCustId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setWallet_balance(customer.getWallet_balance().add(rechargeDto.getAmount()));
        customerRepository.save(customer);
    }

    private void checkPrivileged(Long custId, String token) {
        customerRepository.findById(custId).orElseThrow();
        Token response = rechargeRepo.findByCustomerAndToken(custId, token).orElseThrow();
        log.info("Checking token expiry -> Current Time: {}, Expiry Time: {}", LocalDateTime.now(), response.getExpiryTimestamp());
        if (LocalDateTime.now().isAfter(response.getExpiryTimestamp())) {
            response.setUsed(true);
            rechargeRepo.save(response);
            throw new RuntimeException("This recharge token has expired.");
        }
        //balance update
    }
}








