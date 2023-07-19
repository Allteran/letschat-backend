package io.allteran.letschatbackend.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String userLogin;
    private String password;
    private String passwordConfirm;
    private String passwordResetToken;
}
