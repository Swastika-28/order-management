package com.project.order_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenId implements Serializable {
    @Column(name = "cust_id")
    private Long customerId;

    @Column(name = "token")
    private String token;
}