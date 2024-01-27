package io.allteran.letschatbackend.security;

import io.allteran.letschatbackend.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {
    @Value("${url.frontend}")
    private String ALLOWED_ORIGIN;

    public static final String[] ENDPOINTS_WHITELIST = {
            "swagger-doc/**",
            "/favicon.ico",
            "/api/v1/static/images/**",
            "/api/v1/interest/",
            "/auth/**",
            "/api-docs/**",
            "/forgot-password/**"
    };

    public static final String[] ENDPOINTS_ADMIN = {
            "/api/v1/*/protected/**"
    };

    private final JwtAuthEntryPoint authEntryPoint;
    private final AuthManager authManager;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtAuthEntryPoint authEntryPoint, AuthManager authManager, JwtRequestFilter jwtRequestFilter) {
        this.authEntryPoint = authEntryPoint;
        this.authManager = authManager;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(handler -> handler.accessDeniedHandler(
                                ((request, response, accessDeniedException) -> log.error("Access denied for request=[{}], response=[{}]", request.toString(),
                                        response.toString(), accessDeniedException))
                        )
                        .authenticationEntryPoint(authEntryPoint))
                //IMPORTANT: if you want to separate your URL with different access - don't forget to use securityMatcher to inform Spring
                //on what URL pattern you want to use next lines of config
//                .securityMatcher("/**")
                .authorizeHttpRequests((authz) ->
                        authz.requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                                .requestMatchers(ENDPOINTS_ADMIN).hasAuthority(Role.ADMIN.getAuthority())
                                .anyRequest().hasAuthority(Role.USER.getAuthority()))
                .authenticationManager(authManager);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
