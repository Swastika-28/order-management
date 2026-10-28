package com.project.order_management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.Id;


import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="customers")
public class Customer {
    @Id
    private Long id;
    private String name;
    private String email;
    private BigDecimal wallet_balance;
}
