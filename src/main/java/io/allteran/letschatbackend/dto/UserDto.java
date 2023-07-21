package io.allteran.letschatbackend.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.view.Views;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "DTO. Entity uses to interact between backend and frontend")
public class UserDto {
    @JsonView(Views.Public.class)
    private String id;
    @NotBlank
    @JsonView(value = {Views.Public.class, Views.Profile.class})
    private String name;
    @NotBlank
    @JsonView(value = {Views.Public.class, Views.Profile.class})
    private String email;
    @NotBlank
    @Size(min = 8, max = 32)
    @JsonView(Views.Internal.class)
    private String password;
    @NotBlank
    @Size(min = 8, max = 32)
    @JsonView(Views.Internal.class)
    private String passwordConfirm;
    @JsonView(Views.Public.class)
    private Set<Role> roles;
    boolean active;
    @Schema(nullable = true, description = "You can ignore this field while creating user on frontend")
    @JsonView(Views.Internal.class)
    private LocalDateTime creationDate;
    @Schema(description = "Stores link to user image. File stores on static DB S3")
    @JsonView(Views.Public.class)
    private String userImage;
}
