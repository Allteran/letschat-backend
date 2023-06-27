package io.allteran.letschatbackend.dto;

import io.allteran.letschatbackend.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Entity uses to interact between backend and frontend")
public class UserDto {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 32)
    private String password;
    @NotBlank
    @Size(min = 8, max = 32)
    private String passwordConfirm;
    private Set<Role> roles;
    boolean active;
    @Schema(nullable = true, description = "You can ignore this field while creating user on frontend")
    private LocalDateTime creationDate;
}
