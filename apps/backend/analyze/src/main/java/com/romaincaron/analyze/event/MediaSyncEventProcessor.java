package com.romaincaron.analyze.event;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import com.romaincaron.analyze.service.client.DataCollectionClient;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSyncEventProcessor {

    private final Neo4jSynchronizationService neo4jSyncService;
    private final MediaNodeService mediaNodeService;
    private final DataCollectionClient dataCollectionClient;

    @KafkaListener(topics = "media-sync-topic", groupId = "analyze-service")
    public void processMediaSyncEvent(MediaSyncEvent event) {
        log.info("Received event: {}", event);

        try {
            switch (event.getType()) {
                case MEDIA_SYNCED:
                    handleMediaSyncedEvent(event);
                    break;

                case BATCH_COMPLETED:
                    log.info("Batch completed for media type: {}", event.getPayload());
                    break;

                case SYNC_COMPLETED:
                    handleSyncCompletedEvent();
                    break;

                default:
                    log.warn("Unknown event type: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error processing event {}: {}", event, e.getMessage(), e);
        }
    }

    private void handleMediaSyncedEvent(MediaSyncEvent event) {
        String externalId = event.getPayload();
        String sourceName = event.getSourceName();
        log.info("Processing media sync for externalId: {}", externalId);

        try {
            // Get media from its external id and source name
            MediaDto mediaDto = dataCollectionClient.getMediaByExternalIdAndSourceName(externalId, sourceName);

            // Sync to Neo4j
            MediaNode mediaNode = neo4jSyncService.syncMediaToNeo4j(mediaDto);
            log.info("Media synchronized in Neo4j: {}", mediaNode.getTitle());
        } catch (Exception e) {
            log.error("Failed to sync media {}: {}", externalId, e.getMessage(), e);
        }
    }

    private void handleSyncCompletedEvent() {
        log.info("Full synchronization completed successfully!");
    }
}