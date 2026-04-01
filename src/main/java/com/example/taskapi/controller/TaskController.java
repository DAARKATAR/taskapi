package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.AppUser;
import com.example.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API for managing tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public TaskResponse create(@RequestBody TaskRequest request, @AuthenticationPrincipal AppUser user) {
        if (user == null) {
            System.out.println("[DEBUG_LOG] FAILED: No authenticated user for POST /api/tasks");
            throw new RuntimeException("User not authenticated");
        }
        System.out.println("[DEBUG_LOG] POST /api/tasks for user: " + user.getEmail() + " | Title: " + request.getTitle());
        TaskResponse response = service.create(request, user);
        System.out.println("[DEBUG_LOG] Task created with ID: " + response.getId());
        return response;
    }

    @GetMapping
    @Operation(summary = "Get all tasks with pagination")
    public Page<TaskResponse> getAll(
            @Parameter(description = "Start date for filtering (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 100, sort = "dueDate") Pageable pageable,
            @AuthenticationPrincipal AppUser user) {
        if (user == null) {
            System.out.println("[DEBUG_LOG] FAILED: No authenticated user for GET /api/tasks");
            throw new RuntimeException("User not authenticated");
        }
        System.out.println("[DEBUG_LOG] GET /api/tasks request for user: " + user.getEmail() + " (ID: " + user.getId() + ")");
        Page<TaskResponse> result = service.getAll(startDate, endDate, user, pageable);
        System.out.println("[DEBUG_LOG] Returning " + result.getNumberOfElements() + " tasks for user: " + user.getEmail());
        return result;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by id")
    public TaskResponse getById(@PathVariable Long id, @AuthenticationPrincipal AppUser user) {
        return service.getById(id, user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public TaskResponse update(@PathVariable Long id, @RequestBody TaskRequest request, @AuthenticationPrincipal AppUser user) {
        return service.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal AppUser user) {
        service.delete(id, user);
    }
}
