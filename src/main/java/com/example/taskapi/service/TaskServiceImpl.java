package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.mapper.TaskMapper;
import com.example.taskapi.model.AppUser;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request, AppUser user) {
        Task task = mapper.toEntity(request);
        task.setUser(user);
        Task savedTask = repository.save(task);
        repository.flush(); // Asegurar que se guarda inmediatamente
        return mapper.toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAll(LocalDate startDate, LocalDate endDate, AppUser user, Pageable pageable) {
        Page<Task> tasks;
        if (startDate != null && endDate != null) {
            tasks = repository.findByDueDateBetweenAndUser(startDate, endDate, user, pageable);
        } else {
            tasks = repository.findByUser(user, pageable);
        }
        
        List<TaskResponse> responses = tasks.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, tasks.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id, AppUser user) {
        Task task = repository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return mapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request, AppUser user) {
        Task task = repository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());
        task.setDueDate(request.getDueDate());
        task.setDate(request.getDate());
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setProjectId(request.getProjectId());
        
        Task updatedTask = repository.save(task);
        repository.flush();
        return mapper.toResponse(updatedTask);
    }

    @Override
    @Transactional
    public void delete(Long id, AppUser user) {
        Task task = repository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        repository.delete(task);
        repository.flush();
    }
}
