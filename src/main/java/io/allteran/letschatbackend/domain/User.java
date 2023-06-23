package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Data
@Document(value = "chat_user")
@Schema(description = "Entity to describe user as it implemented in database. ONLY FOR BACKEND PURPOSES")
public class User implements UserDetails {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 8, max = 32)
    private String password;
    @Transient
    @NotBlank
    @Size(min = 8, max = 32)
    private String passwordConfirm;
    private Set<Role> roles;
    boolean active;
    @Schema(nullable = true, description = "You can ignore this field while creating user on frontend")
    private LocalDateTime creationDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
