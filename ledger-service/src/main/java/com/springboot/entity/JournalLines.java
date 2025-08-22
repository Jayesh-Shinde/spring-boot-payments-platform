package com.springboot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@EntityListeners(EntityListeners.class)
@Data
@NoArgsConstructor
@Table(name = "journal_lines", schema = "public")
public class JournalLines {
    public JournalLines(UUID journalId, UUID accountId, BigDecimal amount, String entryType) {
        this.journalId = journalId;
        this.accountId = accountId;
        this.amount = amount;
        this.entryType = entryType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;
    @Column(nullable = false, name = "journal_id")
    @NotNull
    private UUID journalId;
    @Column(nullable = false, name = "account_id")
    @NotNull
    private UUID accountId;
    @Column(nullable = false)
    @NotNull
    private BigDecimal amount;
    @Column(nullable = false, name = "entry_type")
    @NotNull
    private String entryType;
}
