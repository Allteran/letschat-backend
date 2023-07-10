package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.payload.AuthRequest;
import io.allteran.letschatbackend.dto.payload.AuthResponse;
import io.allteran.letschatbackend.dto.payload.UserVerificationRequest;
import io.allteran.letschatbackend.dto.payload.UserVerificationResponse;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.UserStateException;
import io.allteran.letschatbackend.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${message.auth.login.fail}")
    private String MESSAGE_AUTH_FAILED;
    @Value("${message.auth.login.success}")
    private String MESSAGE_AUTH_SUCCESS;
    @Value("${message.auth.signup.success}")
    private String MESSAGE_SIGNUP_SUCCESS;
    @Value("${message.auth.unverified}")
    private String MESSAGE_AUTH_UNVERIFIED;
    @Value("${verification.message.success}")
    private String MESSAGE_USER_VERIFICATION_SUCCESS;
    @Value("${verification.message.fail}")
    private String MESSAGE_USER_VERIFICATION_FAIL;
    @Value("${verification.message.resent.success}")
    private String MESSAGE_VERIFICATION_RESENT_SUCCESSFULLY;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenService passwordResetTokenService;

    public AuthResponse login(AuthRequest request) {
        User user = userService.findByEmail(request.getLogin());
        if(user == null) {
            return new AuthResponse(request.getLogin(), null, null, MESSAGE_AUTH_FAILED, AuthResponse.Status.AUTH_FAILED);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(request.getLogin(), null, null, MESSAGE_AUTH_FAILED, AuthResponse.Status.AUTH_FAILED);
        }
        if(!user.isActive()) {
            return new AuthResponse(request.getLogin(), null, null, MESSAGE_AUTH_UNVERIFIED, AuthResponse.Status.USER_STATE_ERROR);
        }

        var token = jwtUtil.generateToken(user);
        return new AuthResponse(request.getLogin(), user.getName(), token, MESSAGE_AUTH_SUCCESS, AuthResponse.Status.LOGGED_IN);
    }

    public AuthResponse loginWithGoogle(AuthRequest request) {
        User user = userService.findByEmail(request.getLogin());
        if(user == null) {
            return new AuthResponse(request.getLogin(), null, null, MESSAGE_AUTH_FAILED, AuthResponse.Status.AUTH_FAILED);
        }
        var token = jwtUtil.generateToken(user);
        return new AuthResponse(request.getLogin(), user.getName(), token, MESSAGE_AUTH_SUCCESS, AuthResponse.Status.LOGGED_IN);
    }

    public AuthResponse registerUser(User user) {
        AuthResponse response = new AuthResponse(user.getEmail(), user.getName(), null, null, null);
        try {
            User createdUser = userService.createUser(user);
            response.setName(createdUser.getName());
            response.setLogin(createdUser.getEmail());
            response.setMessage(MESSAGE_SIGNUP_SUCCESS);
            response.setStatus(AuthResponse.Status.REGISTERED);
        } catch (EntityFieldException ex) {
            response.setMessage(ex.getMessage());
            response.setStatus(AuthResponse.Status.USER_FILED_ERROR);
        } catch (UserStateException ex) {
            response.setMessage(ex.getMessage());
            response.setStatus(AuthResponse.Status.USER_STATE_ERROR);
        } catch (InternalException ex) {
            response.setMessage(ex.getMessage());
            response.setStatus(AuthResponse.Status.INTERNAL_ERROR);
        }
        return response;
    }

    public AuthResponse registerWithGoogle(User user) {
        AuthResponse response = new AuthResponse(user.getEmail(), user.getName(), null, null, null);
        try {
            User createdUser = userService.createUserWithGoogle(user);
            response.setName(createdUser.getName());
            response.setLogin(createdUser.getEmail());
            response.setMessage(MESSAGE_SIGNUP_SUCCESS);
            response.setStatus(AuthResponse.Status.REGISTERED);
        } catch (EntityFieldException ex) {
            response.setMessage(ex.getMessage());
            response.setStatus(AuthResponse.Status.USER_FILED_ERROR);
        } catch (UserStateException ex) {
            response.setMessage(ex.getMessage());
            response.setStatus(AuthResponse.Status.USER_STATE_ERROR);
        }
        return response;
    }

    public UserVerificationResponse resendVerificationCode(String email, String username) {
        try {
            userService.sendVerificationCode(email, username);
            return new UserVerificationResponse(email, MESSAGE_VERIFICATION_RESENT_SUCCESSFULLY);
        } catch (MessagingException | IOException ex) {
            return new UserVerificationResponse(email, ex.getMessage());
        }
    }

    public UserVerificationResponse verifyUser(UserVerificationRequest request) {
        return (userService.verifyUser(request.getLogin(), request.getCode()))
                ? new UserVerificationResponse(request.getLogin(), MESSAGE_USER_VERIFICATION_SUCCESS)
                : new UserVerificationResponse(request.getLogin(), MESSAGE_USER_VERIFICATION_FAIL);
    }

    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (JwtException jwtException) {
            return false;
        }
    }


}
