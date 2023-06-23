package io.allteran.letschatbackend.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("user_verification_code")
public class UserVerificationCode {
    @Id
    private String id;
    private String userLogin;
    private long verificationCode;
    private int attemptsCount;
    private Date creationDate;
}
