package com.example.taskapi.service;

import com.example.taskapi.model.AppUser;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.AppUserRepository;
import com.example.taskapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskBotService {

    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;

    private static final String BOT_EMAIL = "bot@example.com";
    private static final String BOT_NAME = "Bot Automático";
    private static final String TASK_TITLE = "Tarea Automática de Prueba";

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Bot en espera de recursos de Render.");
    }

    // @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runBotTask() {
        log.info("Bot desactivado por optimización.");
    }
}
