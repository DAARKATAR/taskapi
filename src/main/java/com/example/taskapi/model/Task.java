package com.example.taskapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_user_id", columnList = "user_id"),
    @Index(name = "idx_task_due_date", columnList = "due_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private boolean completed;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "due_date")
    private LocalDate dueDate;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String projectId;
}
