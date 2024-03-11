package io.allteran.letschatbackend.dto;

import io.allteran.letschatbackend.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Data
@Schema(description = "DTO. Entity uses to CREATE user")
public class UserSignUpDto {
    private String id;
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @NotBlank
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @NotBlank
    @Size(min = 8, max = 32)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @NotBlank
    @Size(min = 8, max = 32)
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String passwordConfirm;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    private Set<Role> roles;
    boolean active;
    @Schema(nullable = true, description = "You can ignore this field while creating user on frontend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime creationDate;
}
