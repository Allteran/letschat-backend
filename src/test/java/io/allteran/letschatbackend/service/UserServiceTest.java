package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.domain.UserVerificationCode;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.UserStateException;
import io.allteran.letschatbackend.repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private UserVerificationCodeService verificationCodeService;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser() {
        //given
        User givenUser = new User(
                "testId",
                "testName",
                "testEmail@mail.com",
                "testPassword",
                "testPassword",
                null,
                true,
                null
        );
        String rawPassword = givenUser.getPassword();
        final String MOCK_PASSWORD="MOCKED_ENCODED_PASSWORD";

        Mockito.when(userRepo.save(givenUser)).thenReturn(givenUser);
        Mockito.when(passwordEncoder.encode(givenUser.getPassword())).thenReturn(MOCK_PASSWORD);

        //when
        User createdUser = userService.createUser(givenUser);

        //then
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(givenUser.getName(), createdUser.getName());
        Assertions.assertNotEquals(rawPassword, createdUser.getPassword());
        Assertions.assertEquals(createdUser.getPasswordConfirm().length(), 0);
        Assertions.assertFalse(createdUser.isActive());
        Assertions.assertTrue(createdUser.getRoles().contains(Role.USER));
        Assertions.assertEquals(createdUser.getRoles().size(), 1);
        assertNotNull(createdUser.getCreationDate());
    }

    @Test
    void createUser_shouldThrow_emailFieldError() {
        //given
        User givenUser = new User(
                "testId",
                "testName",
                "wrongEmail@",
                "testPassword",
                "testPassword",
                Set.of(Role.USER),
                false,
                LocalDateTime.now()
        );

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(EntityFieldException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        });
        Assertions.assertNull(notCreatedUser[0]);
    }

    @Test
    void createUser_shouldThrow_nameFieldError() {
        //given
        User givenUser = new User(
                "testId",
                "",
                "testEmail@mail.com",
                "testPassword",
                "testPassword",
                Set.of(Role.USER),
                false,
                LocalDateTime.now()
        );
        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(EntityFieldException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        });
        Assertions.assertNull(notCreatedUser[0]);
    }

    @Test
    void createUser_shouldThrow_userExistNotVerified() {
        //given
        User givenUser = new User(
                "testId",
                "testName",
                "testEmail@mail.com",
                "testPassword",
                "testPassword",
                Set.of(Role.USER),
                false,
                LocalDateTime.now()
        );

        //given
        Mockito.when(userRepo.findByEmail(givenUser.getEmail())).thenReturn(givenUser);

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(UserStateException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        }, "User creation thrown UserStateException");
        Assertions.assertNull(notCreatedUser[0], "User not created");
    }

    @Test
    void createUser_shouldThrow_userExistAndActive() {
        //given
        User givenUser = new User(
                "testId",
                "testName",
                "testEmail@mail.com",
                "testPassword",
                "testPassword",
                Set.of(Role.USER),
                true,
                LocalDateTime.now()
        );

        //when
        Mockito.when(userRepo.findByEmail(givenUser.getEmail())).thenReturn(givenUser);

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(EntityFieldException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        }, "User not created, because email isn't unique");
        Assertions.assertNull(notCreatedUser[0], "Creation did not succeed, user is null");
    }

    @SneakyThrows
    @Test
    void createUser_shouldThrow_mailingException() {
        //given
        User givenUser = new User(
                "testId",
                "testName",
                "testEmail@mail.com",
                "testPassword",
                "testPassword",
                Set.of(Role.USER),
                true,
                LocalDateTime.now()
        );
        UserVerificationCode code = new UserVerificationCode(
                "testCodeId",
                givenUser.getEmail(),
                111111,
                0,
                new Date()
        );
        //when
        Mockito.when(verificationCodeService.createCode(givenUser.getEmail())).thenReturn(code);
        Mockito.doThrow(MessagingException.class).when(emailService).sendVerificationEmail(code, givenUser.getUsername());

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(InternalException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        }, "User not created because of internal exception");
        Assertions.assertNull(notCreatedUser[0], "Creation did not succeed, user is null");
    }


}