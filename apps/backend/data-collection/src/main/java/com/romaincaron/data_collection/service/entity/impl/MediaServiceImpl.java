package com.romaincaron.data_collection.service.entity.impl;

import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.repository.MediaRepository;
import com.romaincaron.data_collection.service.entity.MediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media findById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));
    }

    @Override
    public List<Media> findAll() {
        return mediaRepository.findAll();
    }

    @Override
    public Media save(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public void delete(Long id) {
        Optional<Media> media = mediaRepository.findById(id);
        media.ifPresent(mediaRepository::delete);
    }

    @Override
    public Optional<Media> findByExternalIdAndSourceName(String externalId, String sourceName) {
        return mediaRepository.findByExternalIdAndSourceName(externalId, sourceName);
    }
}
