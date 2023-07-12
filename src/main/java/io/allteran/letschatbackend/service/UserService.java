package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.domain.UserVerificationCode;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.exception.UserStateException;
import io.allteran.letschatbackend.repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationCodeService verificationCodeService;
    private final EmailService emailService;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }

    @Transactional
    public User createUserWithGoogle(User user) {
        if(user.getName().isBlank() || user.getName().isEmpty()) {
            throw new EntityFieldException("User creation error: name is required");
        }
        User existedUser = findByEmail(user.getEmail());
        if(existedUser != null) {
            throw new EntityFieldException("User creation error: email must be unique");
        }
        user.setPassword("");
        user.setPasswordConfirm("");
        user.setCreationDate(LocalDateTime.now());
        user.setActive(true);
        user.setRoles(Set.of(Role.USER));
        return repo.save(user);
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
        User existedUser = findByEmail(user.getEmail());
        if(existedUser != null) {
            if(!existedUser.isActive()) {
                throw new UserStateException("User is not verified");
            }
            throw new EntityFieldException("User creation error: email must be unique");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordConfirm("");
        user.setCreationDate(LocalDateTime.now());
        user.setActive(false);
        user.setRoles(Set.of(Role.USER));

        try {
            sendVerificationCode(user.getEmail(), user.getName());
            return repo.save(user);
        } catch (MessagingException | IOException e) {
            throw new InternalException(e.getMessage());
        }
    }

    public void sendVerificationCode(String email, String username) throws MessagingException, IOException {
        UserVerificationCode code = verificationCodeService.createCode(email);
        emailService.sendVerificationEmail(code, username);
    }

    @Transactional
    public boolean verifyUser(String login, long code) {
        boolean codeVerified = verificationCodeService.verify(login, code);
        if(codeVerified) {
            User user = repo.findByEmail(login);
            user.setActive(true);
            repo.save(user);
        }
        return codeVerified;
    }

    private boolean emailValidation(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    @Transactional
    public boolean changePasswordFromReset(String userLogin, String password, String passwordConfirm) throws NotFoundException, EntityFieldException{
        User user = findByEmail(userLogin);
        if(user == null) {
            throw new NotFoundException("User not found");
        }
        if(!password.equals(passwordConfirm)) {
            throw new EntityFieldException("Passwords don't match");
        }

        user.setPassword(passwordEncoder.encode(password));
        repo.save(user);
        return true;
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Transactional
    public User saveUserImage(String userId, String userImage) {
        Optional<User> userOptional = repo.findById(userId);
        if(userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOptional.get();
        user.setUserImage(userImage);
        return repo.save(user);
    }
}
