package io.allteran.letschatbackend.config;

import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.domain.User;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.time.LocalDateTime;
import java.util.Set;
@TestConfiguration
public class SecurityTestConfig {
    @Bean(name = "testSecurityFilterChain")
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //IMPORTANT: if you want to separate your URL with different access - don't forget to use securityMatcher to inform Spring
                //on what URL pattern you want to use next lines of config
                .securityMatcher("/**")
                .authorizeHttpRequests((authz) ->

                        authz.anyRequest().authenticated())
                .cors(Customizer.withDefaults())
//                .exceptionHandling()
//                .authenticationEntryPoint(authEntryPoint)
                .csrf().disable()
                .formLogin().disable();

        Filter filter = (servletRequest, servletResponse, filterChain) -> {
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            UserDetails user = new User(
                    "testId",
                    "testUser",
                    "test@mail.com",
                    "password",
                    "password",
                    Set.of(Role.USER),
                    true, LocalDateTime.now(),
                    null
            );
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(servletRequest, servletResponse);
        };
        httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
