package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.*;
import io.allteran.letschatbackend.service.AuthService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    @PostMapping("/signUp")
    public ResponseEntity<UserDto> singUp(@RequestBody UserDto body) {
        return ResponseEntity.ok(EntityMapper.convertToDto(authService.registerUser(EntityMapper.convertToEntity(body))));
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User verified email successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserVerificationResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error: invalid input data or verification code expired",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserVerificationResponse.class))}
            )
    })
    @PostMapping("/userVerify")
    public ResponseEntity<UserVerificationResponse> verifyUser(@RequestBody UserVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyUser(request));
    }



}
