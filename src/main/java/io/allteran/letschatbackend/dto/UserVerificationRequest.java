package io.allteran.letschatbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserVerificationRequest {
    @NotBlank
    private String login;
    @NotBlank
    private long code;
}
