package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.AuthRequest;
import io.allteran.letschatbackend.dto.AuthResponse;
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

//    public Mono<AuthResponse> login(String login, String password) {
//        return userService.findByEmail(login)
//                .singleOptional()
//                .flatMap(userOptional -> {
//                    if(userOptional.isEmpty()) {
//                        return Mono.just(new AuthResponse(login, null, MESSAGE_AUTH_FAILED));
//                    }
//                    User user = userOptional.get();
//                    if(!passwordEncoder.matches(password, user.getPassword())) {
//                        return Mono.just(new AuthResponse(login, null, MESSAGE_AUTH_FAILED));
//                    }
//
//                    var token = jwtUtil.generateToken(user);
//                    return Mono.just(new AuthResponse(login, token, MESSAGE_AUTH_SUCCESS));
//                });
//    }
//
//    public Mono<String> validateToken(String token) {
//        try{
//
//            return jwtUtil.validateToken(token) ? Mono.just(MESSAGE_TOKEN_VALID)
//                    : Mono.error(new TokenException(MESSAGE_TOKEN_INVALID));
//        } catch (JwtException jwtException) {
//            return Mono.error(new TokenException(MESSAGE_TOKEN_INVALID));
//        }
//    }

    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (JwtException jwtException) {
            return false;
        }
    }
}
