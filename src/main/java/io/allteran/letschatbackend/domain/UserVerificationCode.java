package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("user_verification_code")
@Schema(name = "UserVerificationCode", description = "Backend entity. Entity to describe UserVerificationCode as it implemented in database.")
public class UserVerificationCode {
    @Id
    private String id;
    @NotBlank
    @NotEmpty
    private String userLogin;
    private long verificationCode;
    @Schema(description = "In future this will be used for limited confirmation requests to prevent DDOS attacks")
    private int attemptsCount;
    private Date creationDate;
}


