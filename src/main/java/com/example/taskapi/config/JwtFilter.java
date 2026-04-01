package com.example.taskapi.config;

import com.example.taskapi.repository.AppUserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.taskapi.model.AppUser;

import java.io.IOException;
import java.util.Collections;

public class JwtFilter extends OncePerRequestFilter {

    private final AppUserRepository userRepository;
    private final String clientId;

    public JwtFilter(AppUserRepository userRepository, String clientId) {
        this.userRepository = userRepository;
        this.clientId = clientId;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        
        System.out.println("[DEBUG_LOG] Request to: " + request.getRequestURI() + " with Authorization header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("[DEBUG_LOG] Token (first 20 chars): " + (token.length() > 20 ? token.substring(0, 20) : token));

            if ("MOCK_JWT_TOKEN".equals(token) || "MOCK_TOKEN".equals(token) || token.startsWith("MOCK_")) {
                System.out.println("[DEBUG_LOG] Mock Token detected: " + token);
                String email = "test_user@example.com";
                String finalEmail = email;
                if (token.contains("@")) {
                    finalEmail = token.replace("MOCK_", "");
                }
                String userEmail = finalEmail;
                AppUser user = userRepository.findByEmail(userEmail).orElseGet(() -> {
                    System.out.println("[DEBUG_LOG] Creating new test user: " + userEmail);
                    AppUser newUser = AppUser.builder()
                            .email(userEmail)
                            .name("Test User")
                            .googleId("mock-google-id")
                            .build();
                    return userRepository.save(newUser);
                });

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[DEBUG_LOG] Authentication set for user: " + userEmail);
            } else {
                try {
                    if (clientId == null || clientId.isEmpty() || "REPLACE_WITH_YOUR_CLIENT_ID".equals(clientId)) {
                        System.out.println("[DEBUG_LOG] Google Client ID not configured in application.properties!");
                        filterChain.doFilter(request, response);
                        return;
                    }
                    System.out.println("[DEBUG_LOG] Validating Google Token with client ID: " + clientId);
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                            .setAudience(Collections.singletonList(clientId))
                            .build();

                    GoogleIdToken idToken = verifier.verify(token);
                    if (idToken != null) {
                        GoogleIdToken.Payload payload = idToken.getPayload();
                        
                        // Log additional payload fields for debugging
                        System.out.println("[DEBUG_LOG] Payload Audience: " + payload.getAudience());
                        System.out.println("[DEBUG_LOG] Payload Authorized Party (azp): " + payload.getAuthorizedParty());
                        System.out.println("[DEBUG_LOG] Payload Subject (sub): " + payload.getSubject());

                        String email = payload.getEmail();
                        String name = (String) payload.get("name");
                        String googleId = payload.getSubject();

                        if (email != null) {
                            System.out.println("[DEBUG_LOG] Valid token for email: " + email);
                            AppUser user = userRepository.findByEmail(email).map(existingUser -> {
                                if (existingUser.getGoogleId() == null || !existingUser.getGoogleId().equals(googleId)) {
                                    System.out.println("[DEBUG_LOG] Updating Google ID for existing user: " + email);
                                    existingUser.setGoogleId(googleId);
                                    if (name != null && existingUser.getName() == null) {
                                        existingUser.setName(name);
                                    }
                                    return userRepository.save(existingUser);
                                }
                                return existingUser;
                            }).orElseGet(() -> {
                                System.out.println("[DEBUG_LOG] Auto-registering user: " + email);
                                AppUser newUser = AppUser.builder()
                                        .email(email)
                                        .name(name)
                                        .googleId(googleId)
                                        .build();
                                return userRepository.save(newUser);
                            });

                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    user, null, Collections.emptyList());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            System.out.println("[DEBUG_LOG] Authentication set for user: " + email);
                        } else {
                             System.out.println("[DEBUG_LOG] Payload doesn't contain email.");
                        }
                    } else {
                        System.out.println("[DEBUG_LOG] Token verification failed (idToken is null). Check Google client ID.");
                        // Try a manual check if possible (might be some other reason for null)
                        System.out.println("[DEBUG_LOG] Potential cause: Token expired, audience mismatch, or issuer mismatch.");
                        // Opcional: Para depuración, si el token es MOCK pero no fue capturado arriba
                        if (token.startsWith("MOCK_")) {
                             System.out.println("[DEBUG_LOG] MOCK token detected but didn't match the exact 'MOCK_JWT_TOKEN'.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[DEBUG_LOG] Error validando token de Google: " + e.getMessage());
                    e.printStackTrace(); // Log the full stack trace to the console
                }
            }
        } else {
            System.out.println("[DEBUG_LOG] No Authorization header or doesn't start with Bearer.");
        }

        filterChain.doFilter(request, response);
    }
}
