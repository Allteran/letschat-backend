package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO. Represents request to backend for user verification process by code")
public class UserVerificationRequest {
    @NotBlank
    private String login;
    @NotBlank
    private long code;
}
