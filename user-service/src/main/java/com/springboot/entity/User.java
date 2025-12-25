package com.springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "public"
)
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false, name = "full_name", length = 150)
    private String fullName;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(nullable = false, length = 20)
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 20)
    private KycStatus kycStatus;
    @CreatedDate
    @Column(nullable = false, name = "created_at", updatable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    public User(String email, String fullName, String phone, UserStatus status, KycStatus kycStatus) {
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.status = status;
        this.kycStatus = kycStatus;
    }
}
