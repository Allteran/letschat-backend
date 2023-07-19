package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(value = "reset_password_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PasswordResetToken", description = "Entity. Uses to help user reset password")
public class PasswordResetToken {
    @Id
    private String id;
    private String token;
    private String userLogin;
    private Date expireDate;
}
