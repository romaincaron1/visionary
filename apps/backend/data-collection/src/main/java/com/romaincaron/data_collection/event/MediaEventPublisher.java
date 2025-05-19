package com.romaincaron.data_collection.event;

import com.romaincaron.data_collection.enums.EventType;
import com.romaincaron.data_collection.enums.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaEventPublisher {

    private final KafkaTemplate<String, MediaSyncEvent> kafkaTemplate;

    @Value("${constants.kafka.sync-topic}")
    private String topic;

    /**
     * Notify when a media is synced
     * @param externalId
     * @param sourceName
     * @param singleUpdate
     */
    public void notifyMediaSynced(String externalId, String sourceName, boolean singleUpdate) {
        MediaSyncEvent event = new MediaSyncEvent(
                EventType.MEDIA_SYNCED,
                externalId,
                sourceName,
                LocalDateTime.now(),
                singleUpdate
        );

        kafkaTemplate.send(topic, event);
        log.debug("Sent MEDIA_SYNCED event for externalId {}", externalId);
    }

    /**
     * Notify when a batch is completed
     * @param mediaType
     */
    public void notifyBatchCompleted(MediaType mediaType) {
        MediaSyncEvent event = new MediaSyncEvent(
                EventType.BATCH_COMPLETED,
                mediaType.name(),
                null,
                LocalDateTime.now(),
                false
        );

        kafkaTemplate.send(topic, event);
        log.debug("Sent BATCH_COMPLETED event for type {}", mediaType.name());
    }

    // Notify when the synchronization is completed
    public void notifySyncCompleted() {
        MediaSyncEvent event = new MediaSyncEvent(
                EventType.SYNC_COMPLETED,
                null,
                null,
                LocalDateTime.now(),
                false
        );
        kafkaTemplate.send(topic, event);
        log.debug("Sent SYNC_COMPLETED event");
    }
}
