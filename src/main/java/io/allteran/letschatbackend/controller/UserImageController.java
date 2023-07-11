package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.service.UserImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
            boolean fileUploaded = userImageService.uploadUserImage(userId, image);
            String message = (fileUploaded) ? "OK": "Failed. Check logs";
            return ResponseEntity.ok(message);
        } catch (InternalException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
