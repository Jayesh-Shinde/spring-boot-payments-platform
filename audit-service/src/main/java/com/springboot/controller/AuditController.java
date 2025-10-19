package com.springboot.controller;

import com.springboot.dto.response.AuditLogResponse;
import com.springboot.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private AuditLogService auditLogService;

    @GetMapping("/{transactionId}")
    public List<AuditLogResponse> getAuditLogs(@PathVariable UUID transactionId) {
        return auditLogService.findByTransactionId(transactionId);
    }
}
