package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.AuthRequest;
import io.allteran.letschatbackend.dto.AuthResponse;
import io.allteran.letschatbackend.dto.UserVerificationRequest;
import io.allteran.letschatbackend.dto.UserVerificationResponse;
import io.allteran.letschatbackend.exception.TokenException;
import io.allteran.letschatbackend.security.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${message.auth.fail}")
    private String MESSAGE_AUTH_FAILED;
    @Value("${message.auth.success}")
    private String MESSAGE_AUTH_SUCCESS;
    @Value("${verification.message.success}")
    private String MESSAGE_USER_VERIFICATION_SUCCESS;
    @Value("${verification.message.fail}")
    private String MESSAGE_USER_VERIFICATION_FAIL;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request) {
        User user = userService.findByEmail(request.getLogin());
        if(user == null) {
            return new AuthResponse(request.getLogin(), null, MESSAGE_AUTH_FAILED);
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new AuthResponse(request.getLogin(), null, MESSAGE_AUTH_FAILED);
        }

        var token = jwtUtil.generateToken(user);
        return new AuthResponse(request.getLogin(), token, MESSAGE_AUTH_SUCCESS);
    }

    public User registerUser(User user) {
        return userService.createUser(user);
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
