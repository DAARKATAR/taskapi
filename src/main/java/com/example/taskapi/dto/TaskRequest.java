package com.example.taskapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String projectId;
}
