package com.springboot.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JournalLineResponse {
    @NotNull
    private UUID id;
    @NotNull
    private UUID accountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String entryType;
}
