package com.romaincaron.analyze.service.entity;

import com.romaincaron.analyze.entity.MediaNode;

import java.util.List;
import java.util.Optional;

public interface MediaNodeService {
    Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName);
    List<MediaNode> findSimilarMedia(String externalId, String sourceName, int limit);
    List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit);
    List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit);
    MediaNode save(MediaNode mediaNode);
}
