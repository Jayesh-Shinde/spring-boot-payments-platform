package com.springboot.service;

import com.springboot.dto.requests.AuditRequest;
import com.springboot.dto.response.AuditLogResponse;
import com.springboot.entity.AuditLog;
import com.springboot.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditRepository auditRepository;

    @Override
    public List<AuditLogResponse> findByTransactionId(UUID transactionId) {

        List<AuditLog> byTransactionId = auditRepository.findByTransactionId(transactionId);
        return byTransactionId.stream().map(auditLog ->
                new AuditLogResponse(auditLog.getId(),
                        auditLog.getTransactionId(),
                        auditLog.getAccountId(),
                        auditLog.getEventType(),
                        auditLog.getDetails(),
                        auditLog.getCreatedAt())).toList();
    }


    public void handleTransactionCreated(AuditRequest auditLog) {
        AuditLog auditLogData = new AuditLog(auditLog.getTransactionId(), auditLog.getEventType(), auditLog.getAccountId(), auditLog.getDetails());
        auditRepository.save(auditLogData);
    }
}
