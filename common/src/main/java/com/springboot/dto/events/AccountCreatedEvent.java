package com.springboot.dto.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {
    private String idempotencyKey;
    private UUID accountId;
    private String ownerName;
    private BigDecimal initialBalance;
}
