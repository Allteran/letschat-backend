package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.*;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.AuthService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @Value("${message.token.valid}")
    private String MESSAGE_TOKEN_VALID;
    @Value("${message.token.invalid}")
    private String MESSAGE_TOKEN_INVALID;

    @Operation(summary = "Login existing user", description = "Current method implements user login behaviour and gives AuthResponse based on password. For better understanding, please check AuthResponse.Status entity")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<String> validateToken(@Param("token") String token) {
        return authService.validateToken(token) ? ResponseEntity.ok(MESSAGE_TOKEN_VALID) : ResponseEntity.status(HttpStatus.FORBIDDEN).body(MESSAGE_TOKEN_INVALID);
    }

    @Operation(summary = "Register a new user",
            description = "This method implements user registration action and gives a response depends on actions. For better understanding, please check AuthResponse.Status entity")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Invalid input data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Internal mailing error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))}

            )
    })
    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> singUp(@RequestBody UserDto body) {
       return ResponseEntity.ok(authService.registerUser(EntityMapper.convertToEntity(body)));
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

    @Operation(summary = "Resend verification code for user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification code sent successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserVerificationResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error: some internal error, check logs",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserVerificationResponse.class))}
            )
    })
    @PostMapping("/resendCode/{email}")
    public ResponseEntity<UserVerificationResponse> resendVerificationCode(@PathVariable("email") String email) {
        return ResponseEntity.ok(authService.resendVerificationCode(email));
    }

    //so for now I didn't get how to implement OAUTH2 with JWT username&password auth, so it will be very bad code rn

    @Operation(summary = "Create account with Google", description = "WILL BE DEPRECATED SOON")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Invalid input data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Internal mailing error",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthResponse.class))}

            )
    })
    @PostMapping("/google/signUp")
    public ResponseEntity<AuthResponse> googleRegister(@RequestBody UserDto body) {
        return ResponseEntity.ok(authService.registerWithGoogle(EntityMapper.convertToEntity(body)));
    }

    @Operation(summary = "Login with Google Account", description = "WILL BE DEPRECATED SOON")
    @PostMapping("/google/login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request));
    }


    @Operation(summary = "Creates request to change password", description = "Current methods checks given data (user email) and checks if there such user in system and creates link to reset the password and sends it to users email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success. Password reset link was sent. As response client gets code and simple message",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error. Link was not sent due to an error of some service",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. User with given email not found"
            )
    })
    @PostMapping("/resetPassword/request")
    public ResponseEntity<String> sendResetPasswordRequest(@RequestParam("email") String email) {
        try {
            return ResponseEntity.ok(authService.resetPassword(email));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @Operation(summary = "Validate reset password token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success. Incoming token is valid for password reset",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "406",
                    description = "Fail. Token is invalid",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            )
    })
    @GetMapping("/resetPassword/validate")
    public ResponseEntity<String> validateResetPasswordToken(@RequestParam("token") String token, @RequestParam("email") String userLogin) {
        if(authService.validateResetPasswordToken(token, userLogin)) {
            return ResponseEntity.ok("SUCCESS");
        } else {
            return ResponseEntity.status(406).body("Token for password reset is invalid");
        }
    }

    @Operation(summary = "Change password from password reset form")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. User not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail. Invalid token for current user or passwords don't match",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping("/resetPassword/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request)  {
        boolean passwordChanged;
        try {
            passwordChanged = authService.changePassword(request);
        } catch (NotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (EntityFieldException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        if(!passwordChanged) {
            return ResponseEntity.status(400).body("Invalid token for current user");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
