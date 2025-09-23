package com.springboot.dto.events;

import com.springboot.dto.requests.TransactionRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private TransactionRequest transactionRequest;
    private String idempotencyKey;
}
