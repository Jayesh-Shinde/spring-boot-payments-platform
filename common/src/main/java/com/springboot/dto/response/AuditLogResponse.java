package com.springboot.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuditLogResponse {
    @NotNull
    private UUID id;
    @NotNull
    private UUID transactionId;
    @NotNull
    private UUID accountId;
    @NotNull
    private String eventType;
    private String details;
    @NotNull
    private Instant createdAt;
}
