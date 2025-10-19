package com.springboot.listener;

import com.springboot.dto.events.TransactionEvent;
import com.springboot.dto.requests.AuditRequest;
import com.springboot.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditListener {
    private final AuditLogService auditLogService;

    @KafkaListener(topics = "transactions.created", groupId = "audit-service")
    public void handleTransactionCreated(TransactionEvent transactionEvent) {
        AuditRequest auditLog = new AuditRequest(transactionEvent.getId(),
                transactionEvent.getTransactionRequest().getFromAccountID(),
                "Transfer",
                "Transaction Created");
        auditLogService.handleTransactionCreated(auditLog);
    }
}
