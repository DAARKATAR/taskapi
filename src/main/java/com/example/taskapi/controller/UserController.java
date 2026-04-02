package com.example.taskapi.controller;

import com.example.taskapi.dto.ProfileRequest;
import com.example.taskapi.model.AppUser;
import com.example.taskapi.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "Endpoints for user profile management")
public class UserController {

    private final AppUserRepository userRepository;

    public UserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/profile")
    @Operation(summary = "Update user profile (country)")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequest request, @AuthenticationPrincipal AppUser user) {
        if (user == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        user.setCountry(request.getCountry());
        userRepository.save(user);
        
        System.out.println("[DEBUG_LOG] Profile updated for user: " + user.getEmail() + " | Country: " + request.getCountry());
        return ResponseEntity.ok(user);
    }
}
