package io.allteran.letschatbackend.security;

import io.allteran.letschatbackend.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthManager implements AuthenticationManager {
    private final JwtUtil jwtUtil;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (JwtException jwtException) {
            username = null;
            log.error("Authenticate: JWT Exception", jwtException);
        }

        if(username != null && jwtUtil.validateToken(token)) {
            Claims claims = jwtUtil.getClaimsFromToken(token);
            Set<Role> roles = claims.get("roles", Set.class);
            return new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    roles
            );
        } else {
            return null;
        }
    }
}
