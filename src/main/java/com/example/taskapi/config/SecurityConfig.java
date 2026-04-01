package com.example.taskapi.config;

import com.example.taskapi.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserRepository userRepository;

    @Value("${google.client.id}")
    private String clientId;

    public SecurityConfig(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOriginPatterns(java.util.List.of(
                    "http://localhost:*", 
                    "https://my-task-pro-manager-daark.vercel.app",
                    "https://task-manager-seven-sepia.vercel.app"
                ));
                config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                config.setAllowedHeaders(java.util.List.of("*"));
                config.setExposedHeaders(java.util.List.of("Authorization", "Content-Type"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/tasks/**").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new JwtFilter(userRepository, clientId), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
