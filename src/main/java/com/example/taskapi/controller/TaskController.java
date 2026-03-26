package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.AppUser;
import com.example.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        return service.create(request, user);
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    public List<TaskResponse> getAll(
            @Parameter(description = "Start date for filtering (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal AppUser user) {
        return service.getAll(startDate, endDate, user);
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
