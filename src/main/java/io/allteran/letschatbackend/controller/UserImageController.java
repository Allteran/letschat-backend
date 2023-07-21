package io.allteran.letschatbackend.controller;

import com.amazonaws.services.s3.Headers;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.UserImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/static/images")
public class UserImageController {
    private final UserImageService userImageService;

    @Operation(summary = "Upload user image")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Image uploaded successfully",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed. Internal error",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal error. Exception with saving image to the S3 server",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class))}
            )
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadUserImage(@RequestParam("userId") String userId, @RequestParam("file")MultipartFile image) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(401).body("UNAUTHORIZED");
        }
        try {
            boolean fileUploaded = userImageService.saveUserImage(userId, image);
            String message = (fileUploaded) ? "OK": "Failed. Check logs";
            return ResponseEntity.ok(message);
        } catch (InternalException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @Operation(summary = "Download user image", description = "Download user image can every logged in user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Image downloaded successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE, schema = @Schema(implementation = ByteArrayResource.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Fail. Internal error has been occurred",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. User does not have an image to download",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE)}
            )
    })
    @GetMapping("/download/{userId}")
    public ResponseEntity<ByteArrayResource> downloadUserImage(@PathVariable("userId") String userId) {
         try {
             byte[] data = userImageService.getUserImage(userId);
             ByteArrayResource resource = new ByteArrayResource(data);
             return ResponseEntity
                     .ok()
                     .contentLength(data.length)
                     .header(Headers.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                     .header(Headers.CONTENT_DISPOSITION, "attachment; filename=\"" + userId + "\"")
                     .body(resource);
         } catch (InternalException ex) {
             ex.printStackTrace();
             return ResponseEntity.status(500).body(null);
         } catch (NotFoundException ex) {
             ex.printStackTrace();
             return ResponseEntity.status(404).body(null);
         }
    }
}
