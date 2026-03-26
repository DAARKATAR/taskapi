package com.example.taskapi.repository;

import com.example.taskapi.model.AppUser;
import com.example.taskapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBetweenAndUser(LocalDate startDate, LocalDate endDate, AppUser user);
    List<Task> findByUser(AppUser user);
}
