package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO. Represents response from backend when user sent verification code to the server")
public class UserVerificationResponse {
    private String login;
    private String message;
}
