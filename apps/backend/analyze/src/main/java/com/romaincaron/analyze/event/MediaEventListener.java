package com.romaincaron.analyze.event;

import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaEventListener {

    private final Neo4jSynchronizationService synchronizationService;
    private final TaskExecutor mediaProcessingExecutor;

    @KafkaListener(topics = "media-events", groupId = "analyze-service")
    public void consumeMediaEvent(MediaEvent event) {
        log.info("Received media event: {}", event);

        mediaProcessingExecutor.execute(() -> {
            try {
                Long mediaId = event.getMediaId();
                String eventType = event.getEventType();

                log.info("Processing media event {} of type {} in background thread", mediaId, eventType);
                synchronizationService.syncMediaToNeo4j(mediaId);
                log.info("Successfully synchronized media {} after {} event", mediaId, eventType);
            } catch (Exception e) {
                log.error("Error processing media event: {}", e.getMessage(), e);
            }
        });
    }


}
