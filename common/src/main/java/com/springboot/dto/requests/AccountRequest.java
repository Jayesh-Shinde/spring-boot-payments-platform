package com.springboot.dto.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class AccountRequest {
    @Size(min = 3, max = 30)
    @NotNull
    private String name;
    @Size(min = 3, max = 30)
    @NotNull
    private String currency;
    @Size(min = 3, max = 30)
    @NotNull
    private String type;
    @NotNull
    @DecimalMin(value = "10000.00")
    private BigDecimal initialAmount;

}
