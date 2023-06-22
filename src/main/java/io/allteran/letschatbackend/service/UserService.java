package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }

    @Transactional
    public User createUser(User user) {
        if(!user.getPassword().equals(user.getPasswordConfirm())) {
            throw new EntityFieldException("User creation error: passwords don't match");
        }
        if(user.getName().isBlank() || user.getName().isEmpty()) {
            throw new EntityFieldException("User creation error: name is required");
        }
        if(!emailValidation(user.getEmail())) {
            throw new EntityFieldException("User creation error: email doesn't match the pattern");
        }
        if(findByEmail(user.getEmail()) != null) {
            throw new EntityFieldException("User creation error: email must be unique");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordConfirm("");
        user.setCreationDate(LocalDateTime.now());
        user.setActive(true);
        return repo.save(user);
    }

//    @Transactional
//    public Mono<User> createUser(Mono<User> userMono) {
//        return userMono.flatMap(user -> {
//            if(!user.getPassword().equals(user.getPasswordConfirm())) {
//                user.setPassword("");
//                user.setPasswordConfirm("");
//                return Mono.error(new EntityFieldException("User creation error: passwords don't match"));
//            }
//            if(user.getName().isBlank() || user.getName().isEmpty()) {
//                return Mono.error(new EntityFieldException("User creation error: name is not declared"));
//            }
//            if(!emailValidation(user.getEmail())) {
//                return Mono.error(new EntityFieldException("User creation error: email doesn't match the pattern"));
//            }
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            user.setPasswordConfirm("");
//            user.setCreationDate(LocalDateTime.now());
//            user.setActive(false);
//            //send email or sms with confirmation code
//            return Mono.just(user);
//        }).flatMap(user -> repo.existsByEmail(user.getEmail())
//                .flatMap(exists -> (exists)
//                        ? Mono.error(new EntityFieldException("User creation error: email should be unique"))
//                        : repo.save(user)));
//
//    }
//
    private boolean emailValidation(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }
}
