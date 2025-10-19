package com.springboot.dto.events;

import com.springboot.dto.requests.TransactionRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private TransactionRequest transactionRequest;
    @NotNull
    private UUID id;
    private String idempotencyKey;
}
