package io.allteran.letschatbackend.dto.payload;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
