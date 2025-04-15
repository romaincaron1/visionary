package com.romaincaron.data_collection.service.synchronization;

import com.romaincaron.data_collection.dto.MediaData;
import com.romaincaron.data_collection.dto.MediaDto;
import com.romaincaron.data_collection.dto.SyncResult;
import com.romaincaron.data_collection.entity.Media;
import com.romaincaron.data_collection.entity.MediaTag;
import com.romaincaron.data_collection.enums.MediaType;
import com.romaincaron.data_collection.event.MediaEventPublisher;
import com.romaincaron.data_collection.mapper.MediaMapper;
import com.romaincaron.data_collection.service.datasource.DataSource;
import com.romaincaron.data_collection.service.datasource.DataSourceManager;
import com.romaincaron.data_collection.service.entity.MediaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class MediaSynchronizationService {

    private final DataSourceManager dataSourceManager;
    private final MediaService mediaService;
    private final GenreSynchronizationService genreService;
    private final TagSynchronizationService tagService;
    private final MediaEventPublisher eventPublisher;

    /**
     * Synchronize all media by a given type
     *
     * @param mediaType
     * @return
     */
    @Transactional
    public SyncResult synchronizeMediaByType(MediaType mediaType) {
        log.info("Starting synchronization for media type: {}", mediaType);
        int totalCreated = 0;
        int totalUpdated = 0;
        int totalErrors = 0;

        for (DataSource dataSource : dataSourceManager.getAvailableSources()) {
            if (!dataSource.isAvailable()) continue;

            try {
                log.info("Fetching {} media data from {}", mediaType, dataSource.getSourceName());
                List<MediaData> mediaDatas = dataSource.fetchAllMedia(mediaType);
                log.info("Fetched {} media data from {}", mediaDatas.size(), dataSource.getSourceName());

                for (MediaData mediaData : mediaDatas) {
                    try {
                        // Utiliser une nouvelle transaction pour chaque média
                        boolean isNew = syncMediaInNewTransaction(mediaData, totalCreated, totalUpdated);
                        if (isNew) totalCreated++; else totalUpdated++;
                    } catch (Exception e) {
                        log.error("Error syncing media {} : {}", mediaData.getExternalId(), e.getMessage(), e);
                        totalErrors++;
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching {} from source {} : {}", mediaType, dataSource.getSourceName(), e.getMessage(), e);
                totalErrors++;
            }
        }

        // Notify when synchronization is completed
        eventPublisher.notifyBatchCompleted(mediaType);

        log.info("Completed synchronization for {}: {} created, {} updated, {} errors",
                mediaType, totalCreated, totalUpdated, totalErrors);

        return new SyncResult()
                .setSuccess(true) // Toujours considérer comme un succès global même s'il y a des erreurs individuelles
                .setMessage(String.format("Processed %d media: %d created, %d updated, %d errors",
                        totalCreated + totalUpdated + totalErrors, totalCreated, totalUpdated, totalErrors));
    }

    /**
     * Synchronize all media by their type
     *
     * @return Map<MediaType, SyncResult>
     */
    @Transactional
    public Map<MediaType, SyncResult> synchronizeAllMediaTypes() {
        log.info("Starting synchronization for media all types ...");
        Map<MediaType, SyncResult> results = new HashMap<>();

        for (MediaType type : MediaType.values()) {
            results.put(type, synchronizeMediaByType(type));
        }

        // Notify that all types has been synchronized
        eventPublisher.notifySyncCompleted();

        log.info("Completed synchronization for media all types ...");
        return results;
    }

    /**
     * Synchronize a media by its externalId
     * @param externalId
     * @param mediaType
     * @param sourceName
     * @return MediaDto
     */
    @Transactional
    public MediaDto synchronizeMediaById(String externalId, MediaType mediaType, String sourceName) {
        log.info("Synchronizing specific media: externalId={}, type={}, source={}", externalId, mediaType, sourceName);

        for (DataSource dataSource : dataSourceManager.getAvailableSources()) {
            if (!dataSource.getSourceName().equals(sourceName)) continue;

            Optional<MediaData> mediaDataOpt = dataSource.fetchMediaById(externalId, mediaType);
            if (mediaDataOpt.isPresent()) {
                MediaData mediaData = mediaDataOpt.get();
                syncSingleMediaData(mediaData);
                eventPublisher.notifyMediaSynced(externalId);

                MediaDto result = mediaService.findByExternalIdAndSourceName(externalId, sourceName)
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve synchronized media"));

                log.info("Successfully synchronized media: {}", result.getTitle());
                return result;
            }
        }

        throw new EntityNotFoundException("Media not found with externalId: " + externalId);
    }


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected boolean syncMediaInNewTransaction(MediaData mediaData, int totalCreated, int totalUpdated) {
        boolean isNew = syncSingleMediaData(mediaData);

        // Notify when media has been synced
        eventPublisher.notifyMediaSynced(mediaData.getExternalId());

        return isNew;
    }

    // Sync a single media based on given data and return if its updated or created
    @Transactional
    protected boolean syncSingleMediaData(MediaData mediaData) {
        Optional<MediaDto> existingMediaDtoOpt = mediaService.findByExternalIdAndSourceName(
                mediaData.getExternalId(), mediaData.getSourceName());

        if (existingMediaDtoOpt.isPresent()) {
            MediaDto mediaDto = existingMediaDtoOpt.get();
            Media media = mediaService.getEntityById(mediaDto.getId());
            syncMediaProperties(media, mediaData);
            return false; // Update
        } else {
            Media media = new Media();
            media.setExternalId(mediaData.getExternalId());
            media.setSourceName(mediaData.getSourceName());
            syncMediaProperties(media, mediaData);
            return true; // Creation
        }
    }

    // Sync media properties with new data
    @Transactional
    protected Media syncMediaProperties(Media media, MediaData mediaData) {
        updateBasicProperties(media, mediaData);

        // First save the media to get an ID
        if (media.getId() == null) {
            try {
                MediaDto mediaDto = MediaMapper.toDto(media);
                mediaDto = mediaService.save(mediaDto);
                media = mediaService.getEntityById(mediaDto.getId());
            } catch (Exception e) {
                log.error("Failed to save media entity: {}", e.getMessage());
                throw e; // Re-throw to trigger transaction rollback for this specific media
            }
        }

        // After media is saved successfully, proceed with relationships
        try {
            // Handle genres
            media.getGenres().clear();
            media.getGenres().addAll(genreService.syncGenres(mediaData.getGenres() != null ?
                    mediaData.getGenres() : Set.of()));

            MediaDto mediaDto = MediaMapper.toDto(media);
            mediaDto = mediaService.save(mediaDto);
            media = mediaService.getEntityById(mediaDto.getId());

            media.getMediaTags().clear();

            mediaDto = MediaMapper.toDto(media);
            mediaDto = mediaService.save(mediaDto);
            media = mediaService.getEntityById(mediaDto.getId());

            if (mediaData.getTags() != null && !mediaData.getTags().isEmpty()) {
                Set<MediaTag> newTags = tagService.createMediaTags(mediaData.getTags(), media);

                mediaDto = MediaMapper.toDto(media);
                mediaDto = mediaService.save(mediaDto);
                media = mediaService.getEntityById(mediaDto.getId());
            }

            return media;
        } catch (Exception e) {
            log.error("Failed to save media relationships: {}", e.getMessage());
            throw e; // Re-throw to trigger transaction rollback for this specific media
        }
    }

    private void updateBasicProperties(Media media, MediaData data) {
        media.setTitle(data.getTitle());
        media.setTitleAlternative(data.getTitleAlternative());
        media.setSynopsis(data.getSynopsis());
        media.setMediaType(data.getMediaType());
        media.setAuthor(data.getAuthor());
        media.setArtist(data.getArtist());
        media.setStartYear(data.getStartYear());
        media.setEndYear(data.getEndYear());
        media.setStatus(data.getStatus());
        media.setCoverUrl(data.getCoverUrl());
        media.setRating(data.getRating());
        media.setPopularity(data.getPopularity());
        media.setLastUpdated(LocalDateTime.now());
    }
}