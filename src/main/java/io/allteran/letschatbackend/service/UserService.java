package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.converter.Converter;
import io.allteran.letschatbackend.domain.*;
import io.allteran.letschatbackend.dto.InterestDto;
import io.allteran.letschatbackend.dto.UserDto;
import io.allteran.letschatbackend.dto.payload.UpdatePasswordRequest;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
//TODO: redo it to facade/adapter pattern to split major work to few dif classes
public class UserService implements UserDetailsService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final ChatLanguageService chatLanguageService;
    private final InterestService interestService;
    private final Converter<InterestDto, Interest> interestConverter;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }

    @Transactional
    public User createUserWithGoogle(User user) {
        if (user.getName().isBlank() || user.getName().isEmpty()) {
            throw new EntityFieldException("User creation error: name is required");
        }
        User existedUser = findByEmail(user.getEmail());
        if (existedUser != null) {
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
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            throw new EntityFieldException("User creation error: passwords don't match");
        }
        if (user.getName().isBlank() || user.getName().isEmpty()) {
            throw new EntityFieldException("User creation error: name is required");
        }
        if (!emailValidation(user.getEmail())) {
            throw new EntityFieldException("User creation error: email doesn't match the pattern");
        }
        User existedUser = findByEmail(user.getEmail());
        if (existedUser != null) {
            if (!existedUser.isActive()) {
                throw new UserStateException("User is not verified");
            }
            throw new EntityFieldException("User creation error: email must be unique");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordConfirm("");
        user.setCreationDate(LocalDateTime.now());
        user.setActive(false);
        user.setRoles(Set.of(Role.PREAUTHORIZED));

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

    public boolean verifyUser(String login, long code) {
        boolean codeVerified = verificationCodeService.verify(login, code);
        if (codeVerified) {
            User user = repo.findByEmail(login);
            user.setActive(true);
            repo.save(user);
        }
        return codeVerified;
    }

    public boolean completeUserRegistration(String login, ChatLanguage language, List<Interest> interests) {
        User user = repo.findByEmail(login);
        if (user == null) {
            throw new NotFoundException("User with mentioned login not found");
        }
        user.setLanguage(language);
        user.setRoles(Set.of(Role.USER));
        user.setInterests(interests);
        return true;
    }

    private boolean emailValidation(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    @Transactional
    public boolean changePasswordFromReset(String userLogin, String password, String passwordConfirm) throws NotFoundException, EntityFieldException {
        User user = findByEmail(userLogin);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        if (!password.equals(passwordConfirm)) {
            throw new EntityFieldException("Passwords don't match");
        }

        user.setPassword(passwordEncoder.encode(password));
        repo.save(user);
        return true;
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return repo.findById(id);
    }

    @Transactional
    public User saveUserImage(String userId, String userImage) {
        Optional<User> userOptional = repo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User user = userOptional.get();
        user.setUserImage(userImage);
        return repo.save(user);
    }

    @Transactional
    public User updateUser(String userId, UserDto updatedUser) {
        Optional<User> userFromDbOp = repo.findById(userId);
        if (userFromDbOp.isEmpty()) {
            throw new NotFoundException("Can't find user with ID [%s]", userId);
        }
        User userFromDb = userFromDbOp.get();

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(userFromDb.getEmail()) &&
                repo.existsByEmail(updatedUser.getEmail())) {
            throw new EntityFieldException(String.format("Given email is already in use [%s]", updatedUser.getEmail()));
        }
        ChatLanguage language = null;
        if (updatedUser.getLanguage() != null) {
            language = chatLanguageService.findByName(updatedUser.getLanguage().getName());
            if (language == null) {
                throw new NotFoundException(String.format("Can't find Language with given name [%s]",
                        updatedUser.getLanguage().getName()));
            }
        }
        List<Interest> interests = updatedUser.getInterests().stream()
                .map(interestDto -> {
                    Optional<Interest> i = interestService.find(interestConverter.convertToEntity(interestDto));
                    if (i.isEmpty()) {
                        throw new NotFoundException(String.format(
                                "Can't find Interest by one of the given params: id = [%s], name = [%s]",
                                interestDto.getId(), interestDto.getName()));
                    }
                    return i.get();
                }).toList();

        //set all fields to user and save it
        userFromDb.setEmail((updatedUser.getEmail()) != null ? updatedUser.getEmail() : userFromDb.getEmail());
        userFromDb.setName(updatedUser.getName());
        userFromDb.setLanguage(language != null ? language : userFromDb.getLanguage());
        userFromDb.setInterests((interests == null || interests.isEmpty()) ? userFromDb.getInterests() : interests);
        return repo.save(userFromDb);
    }

    @Transactional
    public boolean updatePassword(String userId, UpdatePasswordRequest request) {
        if (request.getCurrentPassword() == null) {
            throw new EntityFieldException("Current password is required");
        }
        if (request.getNewPassword() == null || request.getNewPasswordConfirm() == null) {
            throw new EntityFieldException("newPassword and newPasswordConfirm are required");
        }
        if (!request.getNewPasswordConfirm().equals(request.getNewPassword())) {
            throw new EntityFieldException("newPassword doesn't match newPasswordConfirm");
        }

        Optional<User> userFromDbOp = repo.findById(userId);
        if (userFromDbOp.isEmpty()) {
            throw new NotFoundException("Can't find user with ID [%s]", userId);
        }
        User userFromDb = userFromDbOp.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), userFromDb.getPassword())) {
            throw new EntityFieldException("Current password is incorrect");
        }

        userFromDb.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return true;
    }

}
