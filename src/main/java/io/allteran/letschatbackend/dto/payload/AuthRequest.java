package io.allteran.letschatbackend.dto.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO. Represents request to backend for user login")
public class AuthRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
