package io.allteran.letschatbackend.dto.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO. Current DTO represent payload for update password requests")
public class UpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
