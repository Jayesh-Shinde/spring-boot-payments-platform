package com.springboot.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountDTO {
    @NotNull
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String currency;
    @NotNull
    private String type;
    private Instant createdAt;
}
