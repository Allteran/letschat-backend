package io.allteran.letschatbackend.config;

import io.allteran.letschatbackend.domain.Role;
import io.allteran.letschatbackend.security.AuthManager;
import io.allteran.letschatbackend.security.JwtAuthEntryPoint;
import io.allteran.letschatbackend.security.JwtRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

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
                .cors(corsConfig -> corsConfigSource())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
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
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(ALLOWED_ORIGIN));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
