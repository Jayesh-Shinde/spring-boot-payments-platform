package com.springboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Table(name = "journals", schema = "public")
public class Journal {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;
    @Column(nullable = false, updatable = false, name = "idempotency_key")
    @NotNull
    private String idempotencyKey;
    @Column(nullable = false)
    private String reference;
    @Column(nullable = false)
    @NotNull
    private String status;

    public Journal(String idempotencyKey, String reference, String status) {
        this.idempotencyKey = idempotencyKey;
        this.reference = reference;
        this.status = status;
    }
}
