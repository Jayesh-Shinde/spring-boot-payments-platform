package com.springboot.dto.requests;

import com.springboot.dto.enums.JournalLineEntryType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JournalLineRequest {
    @org.hibernate.validator.constraints.UUID
    @NotNull
    private UUID accountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private JournalLineEntryType entryType;
}
