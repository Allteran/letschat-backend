package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.converter.Converter;
import io.allteran.letschatbackend.domain.ChatLanguage;
import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.ChatLanguageDto;
import io.allteran.letschatbackend.dto.InterestDto;
import io.allteran.letschatbackend.dto.UserDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.dto.payload.UpdatePasswordRequest;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RequestMapping("api/v1/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final Converter<UserDto, User> userConverter;
    private final Converter<InterestDto, Interest> interestConverter;
    private final Converter<ChatLanguageDto, ChatLanguage> languageConverter;


    @Operation(summary = "Update profile for logged in user. Note, this method DOES NOT update password")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User has been updated successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can't update user, due to incorrect field"
            )
    })
    @PostMapping("/update/profile")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal User user, @RequestBody UserDto updatedDto) {
        try {
            User updateResult = userService.updateUser(user.getId(), updatedDto);
            UserDto updatedUser = userConverter.convertToDTO(updateResult);
            updatedUser.setLanguage(languageConverter.convertToDTO(updateResult.getLanguage()));
            updatedUser.setInterests(updateResult.getInterests().stream().map(interestConverter::convertToDTO).toList());
            return ResponseEntity.ok(new GeneralResponse<>("SUCCESS", Collections.singletonList(updatedUser)));
        } catch (EntityFieldException | NotFoundException ex) {
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

    @Operation(summary = "Update password for logged in user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User password updated successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Boolean.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Can't update user password, due to incorrect field"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Can't update user password , due to internal errors"
            )
    })
    @PostMapping("/update/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal User user, @RequestBody UpdatePasswordRequest request) {
        try {
            boolean updated = userService.updatePassword(user.getId(), request);
            return updated ? ResponseEntity.ok(new GeneralResponse<>("SUCCESS", Collections.emptyList()))
                    : ResponseEntity.status(500).body(new GeneralResponse<>("INTERNAL ERROR", Collections.singletonList(Boolean.TRUE)));
        } catch (EntityFieldException | NotFoundException ex) {
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }
}
