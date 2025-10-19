package com.springboot.service;

import com.springboot.dto.requests.AuditRequest;
import com.springboot.dto.response.AuditLogResponse;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {
    List<AuditLogResponse> findByTransactionId(UUID transactionId);

    void handleTransactionCreated(AuditRequest auditLog);
}
