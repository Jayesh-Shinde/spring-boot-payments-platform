package com.springboot.repository;

import com.springboot.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByTransactionId(UUID transactionId);
}
