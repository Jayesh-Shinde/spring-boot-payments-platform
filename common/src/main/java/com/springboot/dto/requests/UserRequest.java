package com.springboot.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotBlank
    @Size(max = 150)
    private String fullName;
    @NotBlank
    @Size(max = 150)
    private String email;
    @NotBlank
    @Size(max = 20)
    private String status;
    @NotBlank
    @Size(max = 20)
    private String phone;
    @NotBlank
    @Size(max = 20)
    private String kycStatus;
}
