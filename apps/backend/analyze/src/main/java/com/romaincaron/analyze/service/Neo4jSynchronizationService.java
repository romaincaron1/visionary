package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.client.DataCollectionService;
import com.romaincaron.analyze.service.synchronization.MediaSynchronizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class Neo4jSynchronizationService {

    private final MediaSynchronizer mediaSynchronizer;
    private final DataCollectionService dataCollectionService;

    /**
     * Synchronize a media to Neo4j using its ID
     * @param mediaId ID of the media
     * @return the synchronized MediaNode
     */
    public MediaNode syncMediaToNeo4j(Long mediaId) {
        log.info("Starting Neo4j synchronization for media ID: {}", mediaId);
        return mediaSynchronizer.synchronize(getMediaDto(mediaId));
    }

    /**
     * Get MediaDto from its ID
     * @param mediaId
     * @return
     */
    private MediaDto getMediaDto(Long mediaId) {
        return dataCollectionService.getMediaById(mediaId);
    }
}
