package com.example.taskapi.repository;

import com.example.taskapi.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    
    @org.springframework.data.jpa.repository.Query("SELECT u.email FROM AppUser u")
    java.util.List<String> findAllEmails();

    long countByCreatedAtAfter(java.time.LocalDateTime date);

    @org.springframework.data.jpa.repository.Query("SELECT u.country as country, COUNT(u) as count FROM AppUser u " +
            "WHERE u.country IS NOT NULL GROUP BY u.country ORDER BY count DESC")
    java.util.List<Object[]> findTopCountries();
}
