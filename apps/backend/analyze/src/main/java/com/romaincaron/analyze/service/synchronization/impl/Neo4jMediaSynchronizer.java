package com.romaincaron.analyze.service.synchronization.impl;

import com.romaincaron.analyze.dto.MediaDto;
import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.mapper.MediaMapper;
import com.romaincaron.analyze.service.client.DataCollectionService;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import com.romaincaron.analyze.service.synchronization.GenreSynchronizer;
import com.romaincaron.analyze.service.synchronization.MediaSynchronizer;
import com.romaincaron.analyze.service.synchronization.TagSynchronizer;
import com.romaincaron.analyze.service.vector.MediaVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class Neo4jMediaSynchronizer implements MediaSynchronizer {

    private final MediaNodeService mediaNodeService;
    private final DataCollectionService dataCollectionService;
    private final GenreSynchronizer genreSynchronizer;
    private final TagSynchronizer tagSynchronizer;
    private final MediaVectorService mediaVectorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaNode synchronize(MediaDto mediaDto) {
        log.info("Synchronizing media: {} (ID: {})", mediaDto.getTitle(), mediaDto.getExternalId());

        // Check if media exists in Neo4j
        Optional<MediaNode> existingMediaOpt = mediaNodeService.findByExternalIdAndSourceName(
                mediaDto.getExternalId(), mediaDto.getSourceName());

        MediaNode mediaNode;
        if (existingMediaOpt.isPresent()) {
            mediaNode = existingMediaOpt.get();
            log.debug("Updating existing media node: {}", mediaNode.getTitle());
            mediaNode = MediaMapper.MAP_TO_MEDIA_NODE(mediaNode, mediaDto);
        } else {
            log.debug("Creating new media node for: {}", mediaDto.getTitle());
            mediaNode = MediaMapper.MAP_TO_MEDIA_NODE(new MediaNode(), mediaDto);
        }

        // Save base entity first to ensure it has an ID
        mediaNode = mediaNodeService.save(mediaNode);

        // Synchronize relationships
        genreSynchronizer.synchronize(mediaNode, mediaDto);
        tagSynchronizer.synchronize(mediaNode, mediaDto);

        // Generate and save the vector
        double[] vector = mediaVectorService.generateMediaVector(mediaNode);
        mediaNode.setVectorFromDoubleArray(vector);

        // Save again with relationships
        return mediaNodeService.save(mediaNode);
    }
}