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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
                null,
                null,
                null,
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
        Assertions.assertTrue(createdUser.getRoles().contains(Role.PREAUTHORIZED));
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
                LocalDateTime.now(),
                null,
                null,
                null
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
                LocalDateTime.now(),
                null,
                null,
                null
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
                LocalDateTime.now(),
                null,
                null,
                null
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
                LocalDateTime.now(),
                null,
                null,
                null
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
                LocalDateTime.now(),
                null,
                null,
                null
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
        Mockito.doThrow(MessagingException.class).when(emailService).sendVerificationEmail(code, givenUser.getName());

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(InternalException.class, () -> {
            notCreatedUser[0] = userService.createUser(givenUser);
        }, "User not created because of internal exception");
        Assertions.assertNull(notCreatedUser[0], "Creation did not succeed, user is null");
    }

    @Test
    void createWithGoogle_shouldCreateUser() {
        //given
        User givenUser = new User(
                "testGoogleId",
                "googleName",
                "googleEmail@mail.com",
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null

        );
        User expectedUser = new User(
                "testGoogleId",
                "googleName",
                "googleEmail@mail.com",
                "",
                "",
                Set.of(Role.USER),
                true,
                LocalDateTime.now(),
                null,
                null,
                null

        );
        Mockito.when(userRepo.save(givenUser)).thenReturn(expectedUser);

        //when
        User createdUser = userService.createUserWithGoogle(givenUser);

        //then
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(expectedUser.getId(), createdUser.getId());
        Assertions.assertEquals(expectedUser.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(expectedUser.getName(), createdUser.getName());
        Assertions.assertEquals(expectedUser.getPassword(), createdUser.getPassword());
        Assertions.assertEquals(expectedUser.getPasswordConfirm(), createdUser.getPasswordConfirm());
        Assertions.assertEquals(expectedUser.getRoles(), createdUser.getRoles());
        Assertions.assertEquals(expectedUser.isActive(), createdUser.isActive());
    }

    @Test
    void createUserWithGoogle_shouldThrow_nameEmpty() {
        //given
        User givenUser = new User(
                "testGoogleId",
                "     ",
                "googleEmail@mail.com",
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null
        );
        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(EntityFieldException.class, () -> {
             notCreatedUser[0] = userService.createUserWithGoogle(givenUser);
        }, "Name shouldn't be empty or blank");
        Assertions.assertNull(notCreatedUser[0]);
    }

    @Test
    void createUserWithGoogle_shouldThrow_userExist() {
        //given
        User givenUser = new User(
                "testGoogleId",
                "googleName",
                "email@email.com",
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                null
        );
        User existedUser = new User(
                "anotherId",
                "anotherName",
                "email@email.com",
                "",
                "",
                Set.of(Role.USER),
                true,
                null,
                null,
                null,
                null
        );
        //when
        Mockito.when(userRepo.findByEmail(givenUser.getEmail())).thenReturn(existedUser);

        //then
        final User[] notCreatedUser = new User[1];
        Assertions.assertThrows(EntityFieldException.class, () -> {
            notCreatedUser[0] = userService.createUserWithGoogle(givenUser);
        });
        Assertions.assertNull(notCreatedUser[0]);
    }

    @Test
    void verifyUser_shouldVerify() {
        //given
        String givenLogin = "simpleUser";
        long givenCode = 123123;
        User existedUser = new User(
                "testId",
                "testName",
                givenLogin,
                "password",
                "password",
                Set.of(Role.USER),
                false,
                null,
                null,
                null,
                null
        );

        Mockito.when(verificationCodeService.verify(givenLogin, givenCode)).thenReturn(true);
        Mockito.when(userRepo.findByEmail(givenLogin)).thenReturn(existedUser);
        //when
        boolean verified = userService.verifyUser(givenLogin, givenCode);

        Assertions.assertTrue(verified);
    }

    @Test
    void verifyUser_shouldFail() {
        //given
        String givenLogin = "simpleUser";
        long givenCode = 123123;
        User existedUser = new User(
                "testId",
                "testName",
                givenLogin,
                "password",
                "password",
                Set.of(Role.USER),
                false,
                null,
                null,
                null,
                null
        );

        Mockito.when(verificationCodeService.verify(givenLogin, givenCode)).thenReturn(false);
        //when
        boolean verified = userService.verifyUser(givenLogin, givenCode);

        Assertions.assertFalse(verified);
    }

    @Test
    void changePasswordFromReset_shouldChange() {
        //given
        String givenEmail = "givenEmail@mail.com";
        String givenPassword = "password";
        String givenPasswordConfirm = "password";

        User existedUser = new User(
                "id",
                "name",
                givenEmail,
                "somePassword",
                "somePassword",
                Set.of(Role.USER),
                true,
                null,
                null,
                null,
                null
        );

        Mockito.when(userRepo.findByEmail(givenEmail)).thenReturn(existedUser);

        //when
        boolean changedPassword = userService.changePasswordFromReset(givenEmail, givenPassword, givenPasswordConfirm);

        Assertions.assertTrue(changedPassword);
    }

    @Test
    void changePasswordFromReset_shouldThrow_userNotFound() {
        //given
        String givenEmail = "givenEmail@mail.com";
        String givenPassword = "password";
        String givenPasswordConfirm = "password";

        Mockito.when(userRepo.findByEmail(givenEmail)).thenReturn(null);

        AtomicBoolean notChangedPassword = new AtomicBoolean(false);
        //then
        Assertions.assertThrows(NotFoundException.class, () -> {
            notChangedPassword.set(userService.changePasswordFromReset(givenEmail, givenPassword, givenPasswordConfirm));
        });

        Assertions.assertFalse(notChangedPassword.get());

    }

    @Test
    void changePasswordFromReset_shouldThrow_passwordsNotEqual() {
        //given
        String givenEmail = "givenEmail@mail.com";
        String givenPassword = "password";
        String givenPasswordConfirm = "notEqualPasswordConfirm";
        User existedUser = new User(
                "id",
                "name",
                givenEmail,
                "somePassword",
                "somePassword",
                Set.of(Role.USER),
                true,
                null,
                null,
                null,
                null
        );

        Mockito.when(userRepo.findByEmail(givenEmail)).thenReturn(existedUser);

        AtomicBoolean notChangedPassword = new AtomicBoolean(false);
        //then
        Assertions.assertThrows(EntityFieldException.class, () -> {
            notChangedPassword.set(userService.changePasswordFromReset(givenEmail, givenPassword, givenPasswordConfirm));
        });

        Assertions.assertFalse(notChangedPassword.get());

    }




}