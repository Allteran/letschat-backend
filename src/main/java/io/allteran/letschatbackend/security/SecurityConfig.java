package io.allteran.letschatbackend.security;

import io.allteran.letschatbackend.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${url.frontend}")
    private String ALLOWED_ORIGIN;

    public static final String[] ENDPOINTS_WHITELIST = {
            "/auth/**",
            "/api-docs/**",
            "swagger-doc/**",
            "/forgot-password/**",
            "/favicon.ico"
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
                //IMPORTANT: if you want to separate your URL with different access - don't forget to use securityMatcher to inform Spring
                //on what URL pattern you want to use next lines of config
                .securityMatcher("/**")
                .authorizeHttpRequests((authz) ->
                        authz.requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                                .requestMatchers(ENDPOINTS_ADMIN).hasAuthority(Role.ADMIN.getAuthority())
                                .anyRequest().hasAuthority(Role.USER.getAuthority()))
                .cors(Customizer.withDefaults())
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authManager);
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
