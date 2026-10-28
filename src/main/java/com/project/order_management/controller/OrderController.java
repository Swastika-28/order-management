package com.project.order_management.controller;

//import com.project.order_management.PropertyReader;
import com.project.order_management.dto.*;
import com.project.order_management.service.OrderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;
//    public OrderController (OrderService orderService){
//        this.orderService=orderService;
//    }


//    @Value(value = "${spring.name}")
//    private String myName;

//    @Value(value = "${spring.age}")
//    private Integer myAge;

//    @Autowired
//    PropertyReader propertyReader;

    @PostMapping("/process")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) throws MessagingException {

        OrderResponseDto orderResponseDto = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping("/details/{custId}/{token}")
    public ResponseEntity<BigDecimal> checkBalance(@PathVariable Long custId,@PathVariable String token){
         return ResponseEntity.status(HttpStatus.OK).body(orderService.checkBalance(custId,token));

    }

    @PostMapping("/recharge")
    public ResponseEntity<String> rechargeAcc(@RequestBody RechargeDto recharge1Dto){
        System.out.println("Received RechargeDto: " + recharge1Dto);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.rechargeWithToken(recharge1Dto));

    }

//    @GetMapping("/hello")
//    public void printProperty() {
//        System.out.println("This is a project by " + propertyReader.getName());
//        System.out.println("My age is" + propertyReader.getAge());
//    }
}
