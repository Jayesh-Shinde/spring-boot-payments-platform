package com.springboot.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @NotNull
    private UUID debitAccountId;
    @NotNull
    private UUID creditAccountId;
    @NotNull
    @DecimalMin("1.00")
    private BigDecimal amount;
}
