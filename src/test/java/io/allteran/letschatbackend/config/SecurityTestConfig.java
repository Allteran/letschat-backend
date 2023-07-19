package io.allteran.letschatbackend.config;

import io.allteran.letschatbackend.domain.Role;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
public class SecurityTestConfig {
    @Bean(name = "testSecurityFilterChain")
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //IMPORTANT: if you want to separate your URL with different access - don't forget to use securityMatcher to inform Spring
                //on what URL pattern you want to use next lines of config
                .securityMatcher("/**")
                .authorizeHttpRequests((authz) ->
                        authz.anyRequest().permitAll())
                .cors(Customizer.withDefaults())
//                .exceptionHandling()
//                .authenticationEntryPoint(authEntryPoint)
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable();
//                .authenticationManager(authManager);
//        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
