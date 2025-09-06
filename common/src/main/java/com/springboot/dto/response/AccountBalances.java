package com.springboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountBalances {
    private UUID accountId;
    private BigDecimal balance;
}
