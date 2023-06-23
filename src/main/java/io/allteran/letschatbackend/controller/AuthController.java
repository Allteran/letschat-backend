package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.*;
import io.allteran.letschatbackend.service.AuthService;
import io.allteran.letschatbackend.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Value("${message.token.valid}")
    private String MESSAGE_TOKEN_VALID;
    @Value("${message.token.invalid}")
    private String MESSAGE_TOKEN_INVALID;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<String> validateToken(@Param("token") String token) {
        return authService.validateToken(token) ? ResponseEntity.ok(MESSAGE_TOKEN_VALID) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE_TOKEN_INVALID);
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> singUp(@RequestBody UserDto body) {
        return ResponseEntity.ok(EntityMapper.convertToDto(authService.registerUser(EntityMapper.convertToEntity(body))));
    }

    @PostMapping("/userVerify")
    public ResponseEntity<UserVerificationResponse> verifyUser(@RequestBody UserVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyUser(request));
    }



}
