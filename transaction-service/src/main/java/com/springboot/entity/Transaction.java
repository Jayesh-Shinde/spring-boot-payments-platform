package com.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions", schema = "public")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false, name = "from_account_id")
    private UUID fromAccountId;
    @Column(nullable = false, name = "to_account_id")
    private UUID toAccountId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String status;
    @CreatedDate
    @Column(nullable = false, name = "created_at", updatable = false)
    private Instant createdAt;

    public Transaction(UUID fromAccountId, UUID toAccountId, BigDecimal amount, String status) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.status = status;
    }
}
