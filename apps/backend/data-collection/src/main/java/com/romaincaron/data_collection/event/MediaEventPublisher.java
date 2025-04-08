package com.romaincaron.data_collection.event;

import com.romaincaron.data_collection.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaEventPublisher {

    private final KafkaTemplate<String, MediaEvent> kafkaTemplate;
    private static final String TOPIC = "media-events";

    /**
     * Publish a media created event
     * @param mediaId
     */
    public void publishMediaCreated(Long mediaId) {
        MediaEvent event = new MediaEvent(
                mediaId,
                "CREATED",
                System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, String.valueOf(mediaId), event);
    }

    /**
     * Publish a media updated event
     * @param mediaId
     */
    public void publishMediaUpdated(Long mediaId) {
        MediaEvent event = new MediaEvent(
                mediaId,
                "UPDATED",
                System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC, String.valueOf(mediaId), event);
    }
}
