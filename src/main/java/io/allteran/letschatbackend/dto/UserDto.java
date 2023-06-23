package io.allteran.letschatbackend.dto;

import io.allteran.letschatbackend.domain.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDto {
    private String id;
    private String name;
    private String email;
    private String password;
    private String passwordConfirm;
    private Set<Role> roles;
    boolean active;
    private LocalDateTime creationDate;
}
