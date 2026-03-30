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
        log.info("Ejecución inmediata del Bot al iniciar el servidor...");
        runBotTask();
    }

    @Scheduled(cron = "0 0 0 * * *") // Una vez al día a medianoche
    @Transactional
    public void runBotTask() {
        log.info("Iniciando tarea del Bot Automático...");

        // 1. Buscar y eliminar tareas previas del bot
        List<Task> existingTasks = taskRepository.findByTitle(TASK_TITLE);
        if (!existingTasks.isEmpty()) {
            log.info("Eliminando {} tareas automáticas previas.", existingTasks.size());
            taskRepository.deleteAll(existingTasks);
        }

        // 2. Asegurar la existencia del usuario Bot
        AppUser botUser = userRepository.findByEmail(BOT_EMAIL)
                .orElseGet(() -> {
                    log.info("Creando usuario Bot...");
                    AppUser newUser = AppUser.builder()
                            .email(BOT_EMAIL)
                            .name(BOT_NAME)
                            .googleId("bot-system-id")
                            .build();
                    return userRepository.save(newUser);
                });

        // 3. Crear la nueva tarea
        Task newTask = Task.builder()
                .title(TASK_TITLE)
                .description("Esta es una tarea generada automáticamente cada 30 minutos.")
                .completed(false)
                .user(botUser)
                .date(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.now())
                .endTime(LocalTime.now().plusHours(1))
                .build();

        taskRepository.save(newTask);
        log.info("Nueva tarea automática creada exitosamente.");
    }
}
