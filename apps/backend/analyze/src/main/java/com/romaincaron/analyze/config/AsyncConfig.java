package com.romaincaron.analyze.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Bean
    public TaskExecutor mediaProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);         // Nombre de threads à maintenir
        executor.setMaxPoolSize(10);         // Taille maximale du pool
        executor.setQueueCapacity(25);       // Taille de la file d'attente avant rejet
        executor.setThreadNamePrefix("kafka-consumer-");
        executor.initialize();
        return executor;
    }
}
