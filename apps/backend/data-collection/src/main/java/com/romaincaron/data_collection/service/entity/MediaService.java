package com.romaincaron.data_collection.service.entity;

import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.entity.Media;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface MediaService {

    /**
     * Find the media by its ID
     * @param id the media ID
     * @return MediaDto object
     * @throws EntityNotFoundException if no media is found with the given ID
     */
    MediaDto findById(Long id);

    /**
     * Find all medias
     * @return List<MediaDto>
     */
    List<MediaDto> findAll();

    /**
     * Save a media
     * @param mediaDTO
     * @return MediaDto
     */
    MediaDto save(MediaDto mediaDTO);

    /**
     * Delete a media with its ID
     * @param id
     */
    void delete(Long id);

    /**
     * Find a media by its external ID and a source name
     * @param externalId
     * @param sourceName
     * @return Optional<MediaDto>
     */
    Optional<MediaDto> findByExternalIdAndSourceName(String externalId, String sourceName);

    /**
     * Get the Media entity by ID - for internal use
     * @param id the media ID
     * @return Media entity
     * @throws EntityNotFoundException if no media is found with the given ID
     */
    Media getEntityById(Long id);
}