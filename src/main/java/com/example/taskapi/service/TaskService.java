package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.model.AppUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    TaskResponse create(TaskRequest request, AppUser user);
    Page<TaskResponse> getAll(LocalDate startDate, LocalDate endDate, AppUser user, Pageable pageable);
    TaskResponse getById(Long id, AppUser user);
    TaskResponse update(Long id, TaskRequest request, AppUser user);
    void delete(Long id, AppUser user);
}
