package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.service.ChatCategoryService;
import io.allteran.letschatbackend.service.InterestService;
import io.allteran.letschatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/tsst")
@RequiredArgsConstructor
public class TestStuffController {
    private final UserService userService;

    @PostMapping("/adc")
    public ResponseEntity<User> createAdmin() {
        User user = new User();
        user.setEmail("vitalii.prozapas@gmail.com");
        user.setPassword("123123123");
        user.setPasswordConfirm("123123123");
        user.setCreationDate(LocalDateTime.now());
        user.setName("root");
        user.setRoles(Set.of(Role.ADMIN, Role.USER));

        return ResponseEntity.ok(userService.createUser(user));
    }
}
