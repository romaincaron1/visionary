package com.romaincaron.analyze.service.entity;

import com.romaincaron.analyze.entity.MediaNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MediaNodeService {
    Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName);
    List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit);
    List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit);
    MediaNode save(MediaNode mediaNode);
    List<MediaNode> findAll();
    Optional<MediaNode> findById(Long id);
    Page<MediaNode> findAllPaginated(Pageable pageable);
    Map<String, Object> getVectorStats();
    List<Map<String, String>> findAllMediaIdentifiers(int skip, int limit);
}
