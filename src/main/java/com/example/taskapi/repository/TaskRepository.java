package com.example.taskapi.repository;

import com.example.taskapi.model.AppUser;
import com.example.taskapi.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByDueDateBetweenAndUser(LocalDate startDate, LocalDate endDate, AppUser user, Pageable pageable);
    Page<Task> findByUser(AppUser user, Pageable pageable);
    List<Task> findByTitle(String title);
    
    // Nueva consulta para recuperación de tareas huérfanas o mal vinculadas
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Task t WHERE t.user.email = :email")
    List<Task> findByUserEmail(@org.springframework.data.repository.query.Param("email") String email);
}
