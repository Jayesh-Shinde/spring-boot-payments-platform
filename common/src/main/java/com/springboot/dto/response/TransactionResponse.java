package com.springboot.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TransactionResponse {
    @NotNull
    private UUID id;
    @NotNull
    private String Status;
}
