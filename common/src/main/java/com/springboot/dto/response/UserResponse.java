package com.springboot.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {
    @NotNull
    private UUID id;
    @NotBlank

    private String fullName;
    @NotBlank

    private String email;
    @NotBlank

    private String status;
    @NotBlank

    private String phone;
    @NotBlank

    private String kycStatus;
    
}
