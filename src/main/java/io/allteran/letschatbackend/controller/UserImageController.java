package io.allteran.letschatbackend.controller;

import com.amazonaws.services.s3.Headers;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.UserImageService;
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
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadUserImage(@RequestParam("userId") String userId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(401).body(null);
        }
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
