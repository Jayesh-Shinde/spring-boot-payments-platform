package com.springboot.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class AuditRequest {
    @NotNull
    private UUID transactionId;
    @NotNull
    private UUID accountId;
    @NotNull
    private String eventType;
    @NotNull
    private String details;
}
