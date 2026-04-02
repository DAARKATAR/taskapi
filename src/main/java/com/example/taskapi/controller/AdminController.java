package com.example.taskapi.controller;

import com.example.taskapi.repository.AppUserRepository;
import com.example.taskapi.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints for administrative tasks")
public class AdminController {

    private final AppUserRepository userRepository;
    private final TaskRepository taskRepository;

    @Value("${admin.secret:REPLACE_IN_RENDER}")
    private String adminSecret;

    public AdminController(AppUserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/users/emails")
    @Operation(summary = "Get list of all registered emails (Admin only)")
    public ResponseEntity<?> getAllUserEmails(@RequestHeader(value = "X-Admin-Secret", required = false) String secret) {
        if (secret == null || !secret.equals(adminSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-Admin-Secret header");
        }
        return ResponseEntity.ok(userRepository.findAllEmails());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get analytical statistics for the dashboard (Admin only)")
    public ResponseEntity<?> getStats(@RequestHeader(value = "X-Admin-Secret", required = false) String secret) {
        if (secret == null || !secret.equals(adminSecret)) {
            System.out.println("[DEBUG_LOG] FAILED: Admin secret mismatch for /api/admin/stats");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing X-Admin-Secret header");
        }

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("users", userRepository.count());
        stats.put("tasks", taskRepository.count());
        stats.put("completedTasks", taskRepository.countByCompletedTrue());
        stats.put("newUsersToday", userRepository.countByCreatedAtAfter(todayStart));

        // Obtener Top Countries (Max 5)
        List<Object[]> topCountriesData = userRepository.findTopCountries();
        List<Map<String, Object>> topCountries = topCountriesData.stream()
                .limit(5)
                .map(obj -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("country", obj[0]);
                    map.put("count", obj[1]);
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.putAll(stats.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        response.put("topCountries", topCountries);

        System.out.println("[DEBUG_LOG] Admin success: Returning stats Dashboard with Top Countries");
        return ResponseEntity.ok(response);
    }
}
