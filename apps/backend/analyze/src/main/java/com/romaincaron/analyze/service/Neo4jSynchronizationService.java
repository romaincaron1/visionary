package com.romaincaron.analyze.service;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.service.synchronization.MediaSynchronizer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class Neo4jSynchronizationService {

    private final MediaSynchronizer mediaSynchronizer;

    /**
     * Synchronize a media to Neo4j using its ID
     * @param mediaDto ID of the media
     * @return the synchronized MediaNode
     */
    public MediaNode syncMediaToNeo4j(MediaDto mediaDto) {
        log.info("Starting Neo4j synchronization for media ID: {}", mediaDto.getId());
        return mediaSynchronizer.synchronize(mediaDto);
    }
}
