package com.romaincaron.analyze.service.entity;

import com.romaincaron.analyze.entity.MediaNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaNodeService {
    Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName);
    List<MediaNode> findSimilarMedia(String externalId, String sourceName, int limit);
    List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit);
    List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit);
    MediaNode save(MediaNode mediaNode);
    List<MediaNode> findAll();
    Optional<MediaNode> findById(Long id);
}
