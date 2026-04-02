package com.example.taskapi.controller;

import com.example.taskapi.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints for administrative tasks")
public class AdminController {

    private final AppUserRepository userRepository;

    @Value("${admin.secret:REPLACE_IN_RENDER}")
    private String adminSecret;

    public AdminController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/emails")
    @Operation(summary = "Get list of all registered emails (Admin only)")
    public ResponseEntity<?> getAllUserEmails(@RequestHeader(value = "X-Admin-Secret", required = false) String secret) {
        if (secret == null || !secret.equals(adminSecret)) {
            System.out.println("[DEBUG_LOG] FAILED: Admin secret mismatch or missing for /api/admin/users/emails");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-Admin-Secret header");
        }

        List<String> emails = userRepository.findAllEmails();
        System.out.println("[DEBUG_LOG] Admin success: Returning " + emails.size() + " emails");
        return ResponseEntity.ok(emails);
    }
}
