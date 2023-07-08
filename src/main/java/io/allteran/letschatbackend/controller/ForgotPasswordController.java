package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.payload.ChangePasswordRequest;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController {
    private PasswordResetService passwordResetService;
    @Operation(summary = "Creates request to change password", description = "Current methods checks given data (user email) and checks if there such user in system and creates link to reset the password and sends it to users email.")
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
                    description = "Fail. User with given email not found",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping("/request")
    public ResponseEntity<String> sendResetPasswordRequest(@RequestParam("email") String email) {
        try {
            return ResponseEntity.ok(passwordResetService.resetPassword(email));
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
    @GetMapping("/validate")
    public ResponseEntity<String> validateResetPasswordToken(@RequestParam("token") String token, @RequestParam("email") String userLogin) {
        if(passwordResetService.validateResetPasswordToken(token, userLogin)) {
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
    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request)  {
        boolean passwordChanged;
        try {
            passwordChanged = passwordResetService.changePassword(request);
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
