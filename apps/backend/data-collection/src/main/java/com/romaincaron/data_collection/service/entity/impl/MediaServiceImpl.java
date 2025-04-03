package com.romaincaron.data_collection.service.entity.impl;

import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.mapper.MediaMapper;
import com.romaincaron.data_collection.repository.MediaRepository;
import com.romaincaron.data_collection.service.entity.MediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MediaDto findById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));
        return MediaMapper.toDto(media);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaDto> findAll() {
        return mediaRepository.findAll().stream()
                .map(MediaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MediaDto save(MediaDto mediaDTO) {
        Media media;
        if (mediaDTO.getId() != null) {
            // Update existing entity
            media = mediaRepository.findById(mediaDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + mediaDTO.getId()));
            MediaMapper.updateEntityFromDto(media, mediaDTO);
        } else {
            // Create new entity
            media = MediaMapper.toEntity(mediaDTO);
        }

        // Handle relationships separately if needed
        // This would typically be done in a more complex service layer

        media = mediaRepository.save(media);
        return MediaMapper.toDto(media);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!mediaRepository.existsById(id)) {
            throw new EntityNotFoundException("Media not found with id: " + id);
        }
        mediaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MediaDto> findByExternalIdAndSourceName(String externalId, String sourceName) {
        return mediaRepository.findByExternalIdAndSourceName(externalId, sourceName)
                .map(MediaMapper::toDto);
    }

    // Entity to DTO conversion methods for direct repository access when needed
    public Media getEntityById(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + id));
    }
}