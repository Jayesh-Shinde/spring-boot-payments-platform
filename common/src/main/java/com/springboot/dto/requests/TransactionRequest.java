package com.springboot.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TransactionRequest {
    @NotNull
    private UUID fromAccountID;
    @NotNull
    private UUID toAccountId;
    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;

}
