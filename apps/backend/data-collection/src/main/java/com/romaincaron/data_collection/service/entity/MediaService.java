package com.romaincaron.data_collection.service.entity;

import com.romaincaron.data_collection.entity.Media;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface MediaService {

    /**
     * Find the media by its ID
     * @param id the media ID
     * @return Media entity
     * @throws EntityNotFoundException if no media is found with the given ID
     */
    Media findById(Long id);

    /**
     * Find all medias
     * @return List<Media>
     */
    List<Media> findAll();

    /**
     * Save a media
     * @param media
     * @return Media
     */
    Media save(Media media);

    /**
     * Delete a media with its ID
     * @param id
     */
    void delete(Long id);

    /**
     * Find a media by its external ID and a source name
     * @param externalId
     * @param sourceName
     * @return Optional<Media>
     */
    Optional<Media> findByExternalIdAndSourceName(String externalId, String sourceName);
}
