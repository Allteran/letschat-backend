package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.service.InterestService;
import io.allteran.letschatbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tsst")
@RequiredArgsConstructor
public class TestStuffController {
    private final UserService userService;
    private final InterestService interestService;

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

//    @PostMapping("/indb")
//    public ResponseEntity<?> fillInterests() {
//        if(interestService.findAll().isEmpty()) {
//            interests().forEach(interestService::create);
//        }
//        return ResponseEntity.ok().build();
//    }
//    private List<Interest> interests() {
//        return Arrays.asList(
//                new Interest("Software"),
//                new Interest("Video games"),
//                new Interest("News"),
//                new Interest("Books"),
//                new Interest("Music"),
//                new Interest("Fashion"),
//                new Interest("Relationships"),
//                new Interest("Education"),
//                new Interest("Finance"),
//                new Interest("Movies"),
//                new Interest("Children"),
//                new Interest("Science"),
//                new Interest("Art"),
//                new Interest("Food"),
//                new Interest("Travel"),
//                new Interest("Psychology")
//        );
//    }

}
