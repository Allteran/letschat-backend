package io.allteran.letschatbackend.dto;

import lombok.Data;

@Data
public class UserVerificationRequest {
    private String login;
    private long code;
}
