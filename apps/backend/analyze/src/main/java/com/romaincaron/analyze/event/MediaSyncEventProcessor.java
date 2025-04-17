package com.romaincaron.analyze.event;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.Neo4jSynchronizationService;
import com.romaincaron.analyze.service.client.DataCollectionClient;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import com.romaincaron.analyze.service.vector.VectorGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaSyncEventProcessor {

    private final Neo4jSynchronizationService neo4jSyncService;
    private final MediaNodeService mediaNodeService;
    private final DataCollectionClient dataCollectionClient;

    private final Set<Long> pendingMediaIds = ConcurrentHashMap.newKeySet();
    private final VectorGenerationService vectorGenerationService;

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
        boolean singleUpdate = event.isSingleUpdate();
        log.info("Processing media sync for externalId: {}", externalId);

        try {
            // Get media from its external id and source name
            MediaDto mediaDto = dataCollectionClient.getMediaByExternalIdAndSourceName(externalId, sourceName);

            // Sync to Neo4j
            MediaNode mediaNode = neo4jSyncService.syncMediaToNeo4j(mediaDto);
            log.info("Media synchronized in Neo4j: {}", mediaNode.getTitle());

            if (singleUpdate) {
                double[] vector = vectorGenerationService.generateMediaVector(mediaNode);
                mediaNode.setVectorFromDoubleArray(vector);
                mediaNodeService.save(mediaNode);
            } else {
                // Set in the waitlist
                if (pendingMediaIds.contains(mediaNode.getId())) {
                    log.info("Already processing media {}, skipping...", mediaNode.getId());
                    return;
                }

                pendingMediaIds.add(mediaNode.getId());
            }
        } catch (Exception e) {
            log.error("Failed to sync media {}: {}", externalId, e.getMessage(), e);
        }
    }

    private void handleSyncCompletedEvent() {
        log.info("Sync completed event received. Generating vectors for {} pending media", pendingMediaIds.size());

        if (pendingMediaIds.isEmpty()) {
            log.info("No pending media to process");
            return;
        }

        // Generate vectors and update media nodes
        try {

            Set<Long> mediaIdsToProcess = new HashSet<>(pendingMediaIds);
            pendingMediaIds.clear();

            // Get
            for (Long mediaId : mediaIdsToProcess) {
                Optional<MediaNode> mediaNode = mediaNodeService.findById(mediaId);
                // If media node found, update its vector
                if (mediaNode.isPresent()) {
                    double[] vector = vectorGenerationService.generateMediaVector(mediaNode.get());
                    mediaNode.get().setVectorFromDoubleArray(vector);
                    mediaNodeService.save(mediaNode.get());
                }
            }

            log.info("Vector generation completed for {} media", mediaIdsToProcess.size());
        } catch (Exception e) {
            log.error("Error generating vectors: {}", e.getMessage(), e);
        }
    }
}