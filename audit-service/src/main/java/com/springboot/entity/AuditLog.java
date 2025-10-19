package com.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(name = "details")
    private String details;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AuditLog(UUID transactionId, String eventType, UUID accountId, String details) {
        this.transactionId = transactionId;
        this.eventType = eventType;
        this.accountId = accountId;
        this.details = details;
    }
}
