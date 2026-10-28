package com.project.order_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "token")
public class Token {

    @EmbeddedId
    private TokenId tokenId;

    @Column(name = "used")
    private boolean used;

    @Column(name = "expiry_timestamp")
    private LocalDateTime expiryTimestamp;
}