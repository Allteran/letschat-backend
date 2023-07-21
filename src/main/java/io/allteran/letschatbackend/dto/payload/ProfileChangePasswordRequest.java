package io.allteran.letschatbackend.dto.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
