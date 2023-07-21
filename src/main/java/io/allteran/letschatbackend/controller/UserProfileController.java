package io.allteran.letschatbackend.controller;

import com.fasterxml.jackson.annotation.JsonView;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.UserDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.dto.payload.ProfileChangePasswordRequest;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.UserService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.allteran.letschatbackend.view.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("api/v1/user/profile")
@RequiredArgsConstructor
public class UserProfileController {
    @Value("${url.static.userimage.path.get}")
    private String URL_PATH_IMAGE;

    private final UserService userService;

    @Operation(summary = "Get users profile", description = "User can get details of own profile. Some fields are invisible for user in purposes of security")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}
            )
    })
    @JsonView(Views.Public.class)
    @GetMapping("/get")
    public ResponseEntity<GeneralResponse<UserDto>> getProfile(HttpServletRequest request) {
        String baseImageUrl = EntityMapper.extractBaseUrl(request) + URL_PATH_IMAGE;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.singletonList(EntityMapper.convertToDto(user, baseImageUrl))));
    }

    @Operation(summary = "Update user profile", description = "User can update some details of own profile - name and email. Request body has to contain 2 fields: email and name, other fields will be ignored")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. User has been updated",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. User with given ID not found. It may be caused by interception into the system",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail. User with given email already exist, email should be unique",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))}
            )
    })
    @JsonView(Views.Public.class)
    @PutMapping("/update")
    //NOTICE that UserDto in this case may contain only UserDto.name and UserDto.email, other fields will be ignored
    public ResponseEntity<GeneralResponse<UserDto>> updateProfile(@RequestBody @JsonView(Views.Profile.class) UserDto user,
                                                                  HttpServletRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String baseImageUrl = EntityMapper.extractBaseUrl(request) + URL_PATH_IMAGE;
        try {
            User updatedProfile = userService.updateProfile(currentUser.getId(), user);
            return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.singletonList(EntityMapper.convertToDto(updatedProfile, baseImageUrl))));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        } catch (EntityFieldException ex) {
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

    @Operation(summary = "Change user password from profile ", description = "User can change own password from profile page")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Password changed successfully",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail. Old password is not correct or new passwords mismatch",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. User with given ID not found in database. Usually it can be caused by internal error or security interception",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody ProfileChangePasswordRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userService.changeUserPassword(currentUser.getId(), request);
            return ResponseEntity.ok("OK");
        } catch (EntityFieldException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }
}
