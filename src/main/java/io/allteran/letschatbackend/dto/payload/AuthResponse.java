package io.allteran.letschatbackend.dto.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO. Represents response from AuthController when user logged in or registered")
public class AuthResponse {
    private String login;
    private String name;
    private String token;
    private String message;
    private Status status;

    @Schema(description = "Current enum is a flag for login or sign up statuses.\n" +
            "LOGGED_IN - successfully login;\n" +
            "REGISTERED - successfully registration;\n" +
            "AUTH_FAILED - failed login. Something went wrong with fields of User: something wrong with given credentials;\n" +
            "USER_STATE_ERROR - registration or login failed. User already exist, but not active;\n" +
            "USER_FILED_ERROR - registration failed. Some of given users field don't match: email doesn't match the pattern, passwords are don't match, etc\n" +
            "INTERNAL_ERROR - for internal errors")
    public enum Status {

        LOGGED_IN, REGISTERED, AUTH_FAILED, USER_STATE_ERROR, USER_FILED_ERROR, INTERNAL_ERROR;
    }
}
