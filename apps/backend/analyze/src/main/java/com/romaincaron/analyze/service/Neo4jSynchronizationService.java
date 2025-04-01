package com.romaincaron.analyze.service;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.synchronization.MediaSynchronizer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class Neo4jSynchronizationService {

    private static final Logger log = LoggerFactory.getLogger(Neo4jSynchronizationService.class);
    private final MediaSynchronizer mediaSynchronizer;

    /**
     * Synchronize a media to Neo4j using its ID
     * @param mediaId ID of the media
     * @return the synchronized MediaNode
     */
    public MediaNode syncMediaToNeo4j(Long mediaId) {
        log.info("Starting Neo4j synchronization for media ID: {}", mediaId);
        return mediaSynchronizer.synchronize(mediaId);
    }
}
