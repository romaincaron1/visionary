package com.romaincaron.analyze.service.entity.impl;

import com.romaincaron.analyze.entity.MediaNode;
import com.romaincaron.analyze.repositories.MediaNodeRepository;
import com.romaincaron.analyze.service.entity.MediaNodeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MediaNodeServiceImpl implements MediaNodeService {

    private final MediaNodeRepository mediaNodeRepository;

    @Override
    public Optional<MediaNode> findByExternalIdAndSourceName(String externalId, String sourceName) {
        return mediaNodeRepository.findByExternalIdAndSourceName(externalId, sourceName);
    }

    @Override
    public List<MediaNode> findSimilarMedia(String externalId, String sourceName, int limit) {
        return mediaNodeRepository.findSimilarMedia(externalId, sourceName, limit);
    }

    @Override
    public List<MediaNode> findMediaWithSimilarGenres(String externalId, String sourceName, int limit) {
        return mediaNodeRepository.findMediaWithSimilarGenres(externalId, sourceName, limit);
    }

    @Override
    public List<MediaNode> findMediaWithSimilarTags(String externalId, String sourceName, int limit) {
        return mediaNodeRepository.findMediaWithSimilarTags(externalId, sourceName, limit);
    }

    public MediaNode save(MediaNode mediaNode) {
        return mediaNodeRepository.save(mediaNode);
    }
}
